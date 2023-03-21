package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.dtos.FacturaRequestDTO;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.helpers.S3ClientHelper;
import com.immortalcrab.cfdi.helpers.S3ReqURLParser;
import com.immortalcrab.cfdi.helpers.S3ResourceFetchHelper;
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

    private static final String XML_MIME_TYPE = "text/xml";
    private static final String XML_FILE_EXTENSION = ".xml";
    ResourceDescriptor rdesc;

    public static Producer obtainSteadyPipeline() throws EngineError {

        S3BucketStorage s3Resources = new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_RESOURCES"));
        S3BucketStorage s3DataLake = new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_DATA_LAKE"));

        ResourceDescriptor rdescriptor = ResourceDescriptor.fetchProfile(s3Resources, System.getenv("PROFILE_RESOURCES"));
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
    protected void saveOnPersistance(IStorage st, PacReply pacResult) throws EngineError {

        final String fileName = String.format("%s/%s.%s", st.getTargetName(), pacResult.getContent().getName(), XML_FILE_EXTENSION);
        byte[] in = pacResult.getContent().getBuffer().toString().getBytes(StandardCharsets.UTF_8);

        st.upload(XML_MIME_TYPE, in.length, fileName, new ByteArrayInputStream(in));
    }

    @Override
    protected PacReply.Content openPayload(final IPayload payload, Pickard pic) throws EngineError {

        S3ReqURLParser reqMeta = S3ReqURLParser.parse(payload.getReq());
        BufferedInputStream bf = this.getStorage().download(payload.getReq());
        InputStreamReader instreamReader = new InputStreamReader(bf, StandardCharsets.UTF_8);
        String[] parts = reqMeta.getParticles();
        Optional<ResourceDescriptor.Issuer> issuer = rdesc.getIssuer(parts[S3ReqURLParser.URIParticles.ISSUER.ordinal()]);
        return pic.route(
                parts[S3ReqURLParser.URIParticles.KIND.ordinal()],
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
    protected String fetchPassword(Map<String, String> issuerAttribs) throws EngineError {

        Optional<String> passwd = Optional.ofNullable(issuerAttribs.get("passwd"));
        return passwd.orElseThrow(() -> new EngineError("The issuer requested is not having a password", ErrorCodes.PIPELINE_NOT_SPINNED_UP));
    }

    private static class Wiring {

        public static <R extends ClientRequest> PacReply fac(R req, IStamp<PacReply> stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String certificateNo) throws EngineError {

            var ic = new FacturaXml((FacturaRequestDTO) req, certificate, signerKey, certificateNo);
            return stamper.impress(ic.toString());
        }
    }
}
