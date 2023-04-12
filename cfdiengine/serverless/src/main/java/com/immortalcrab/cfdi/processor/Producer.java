package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.dtos.FacturaRequestDTO;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.helpers.S3ClientHelper;
import com.immortalcrab.cfdi.helpers.S3ReqURLParser;
import com.immortalcrab.cfdi.helpers.S3ResourceFetchHelper;
import com.immortalcrab.cfdi.processor.ResourceDescriptor.*;
import com.immortalcrab.cfdi.xml.FacturaXml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Producer extends Processor {

    private static final String XML_FILE_EXTENSION = "xml";
    private static final String XML_MIME_TYPE = String.format("text/%s", XML_FILE_EXTENSION);
    ResourceDescriptor rdesc;

    private static String findMandatoryEnv(final String variableEnv) throws EngineError {

        var value = Optional.ofNullable(System.getenv(variableEnv))
                .orElseThrow(() -> new EngineError(String.format("mandatory %s env variable was not found", variableEnv),
                ErrorCodes.PIPELINE_NOT_SPINNED_UP));

        log.info(String.format("Found mandatory env var [%s => %s]", variableEnv, value));
        return value;
    }

    public static Producer obtainSteadyPipeline() throws EngineError {

        S3BucketStorage s3Resources = new S3BucketStorage(S3ClientHelper.setupWithEnv(false), findMandatoryEnv("BUCKET_RESOURCES"));
        S3BucketStorage s3DataLake = new S3BucketStorage(S3ClientHelper.setupWithEnv(false), findMandatoryEnv("BUCKET_DATALAKE"));

        ResourceDescriptor rdescriptor = ResourceDescriptor.fetchProfile(s3Resources, findMandatoryEnv("PROFILE_RESOURCES"));
        s3Resources.setPathPrefixes(rdescriptor.getPrefixes().turnIntoMap());

        ResourceDescriptor.Pac pac = rdescriptor.getPacSettings(System.getenv("PAC")).orElseThrow(() -> new EngineError("The pac requested is not registered", ErrorCodes.PIPELINE_NOT_SPINNED_UP));

        return new Producer(
                rdescriptor,
                PacSapienStamp.setup(pac.getCarrier(), pac.getLogin(), pac.getPasswd()),
                s3DataLake,
                s3Resources);
    }

    Producer(ResourceDescriptor rdesc, final IStamp stamper, final IStorage storage, final IStorage resources) throws EngineError {

        this(rdesc, stamper, storage, resources,
                Map.of(
                        "fac", new Stages<>((IDecodeStep) (InputStreamReader reader) -> {
                            try (reader) {
                                return new FacturaRequestDTO(reader);
                            } catch (IOException ex) {
                                log.error("Factura request DTO could not see the light");
                                throw new EngineError("Factura request can not be decoded", ErrorCodes.REQUEST_INVALID);
                            }
                        }, Wiring::fac)));
    }

    Producer(ResourceDescriptor rdesc, final IStamp stamper, final IStorage storage,
            final IStorage resources, Map<String, Stages<? extends IDecodeStep, ? extends IXmlStep>> scenarios) throws EngineError {

        super(stamper, storage, resources, scenarios);
        this.rdesc = rdesc;
    }

    @Override
    protected void saveOnPersistance(IStorage st, final String prefix, PacReply pacResult) throws EngineError {

        final String fileName = String.format("%s/%s", prefix, pacResult.getName());
        byte[] in = pacResult.getBuffer().toString().getBytes(StandardCharsets.UTF_8);

        st.upload(XML_MIME_TYPE, in.length, fileName, new ByteArrayInputStream(in));
    }

    @Override
    protected PacReply openPayload(final IPayload payload, Pickard pic) throws EngineError {

        S3ReqURLParser reqMeta = S3ReqURLParser.parse(payload.getReq());
        BufferedInputStream bf = this.getStorage().download(reqMeta.getKey());
        InputStreamReader instreamReader = new InputStreamReader(bf, StandardCharsets.UTF_8);
        String[] parts = reqMeta.getParticles();
        Optional<ResourceDescriptor.Issuer> issuer = rdesc.getIssuer(parts[S3ReqURLParser.URIParticles.ISSUER.ordinal()]);
        return pic.route(
                parts[S3ReqURLParser.URIParticles.KIND.ordinal()],
                parts[S3ReqURLParser.URIParticles.LABEL.ordinal()],
                parts[S3ReqURLParser.URIParticles.ISSUER.ordinal()],
                issuer.orElseThrow(
                        () -> new EngineError("The issuer requested is not registered",
                                ErrorCodes.REQUEST_INVALID)).turnIntoMap(), instreamReader);
    }

    @Override
    protected BufferedInputStream fetchCert(IStorage resources, Map<String, String> issuerAttribs) throws EngineError {

        return S3ResourceFetchHelper.obtainCert(resources, issuerAttribs);
    }

    @Override
    protected BufferedInputStream fetchKey(IStorage resources, Map<String, String> issuerAttribs) throws EngineError {

        return S3ResourceFetchHelper.obtainKey(resources, issuerAttribs);
    }

    @Override
    protected String fetchCertNumber(Map<String, String> issuerAttribs) throws EngineError {

        Optional<String> baseName = Optional.ofNullable(issuerAttribs.get(Issuer.K_CER));
        String cName = baseName.orElseThrow(() -> new EngineError("The issuer's resource can not be obtained", ErrorCodes.PIPELINE_NOT_SPINNED_UP));
        int pos = cName.lastIndexOf('.');
        if (pos > -1) {
            return cName.substring(0, pos);
        }

        return cName;
    }

    private static class Wiring {

        public static <R extends ClientRequest> PacReply fac(R req, final String label, IStamp<PacReply> stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String certificateNo) throws EngineError {

            var ic = new FacturaXml((FacturaRequestDTO) req, certificate, signerKey, certificateNo);
            final String xmlPriorToStamp = ic.toString();
            log.debug(String.format("This how the xml looks prior to stamp -- {{ %s }}", xmlPriorToStamp));
            return stamper.impress(String.format("%s.%s", label, XML_FILE_EXTENSION), xmlPriorToStamp);
        }
    }
}
