package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.utils.S3ReqURLParser;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.javatuples.Pair;

@Log4j2
public class Producer extends Pipeline {

    private static final String XML_MIME_TYPE = "text/xml";
    private static final String XML_FILE_EXTENSION = ".xml";
    ResourceDescriptor rdesc;

    public static Producer obtainSteadyPipeline() throws StorageError, DecodeError {

        S3BucketStorage s3Resources = new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_RESOURCES"));
        S3BucketStorage s3DataLake = new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_DATA_LAKE"));

        ResourceDescriptor rdescriptor = ResourceDescriptor.fetchProfile(s3Resources, System.getenv("PROFILE_RESOURCES"));
        s3Resources.setPathPrefixes(rdescriptor.getPrefixes().turnIntoMap());

        ResourceDescriptor.Pac pac = rdescriptor.getPacSettings(System.getenv("PAC")).orElseThrow(() -> new StorageError("The pac requested is not registered"));

        return new Producer(
                rdescriptor,
                PacRegularStamp.setup(pac.getCarrier(), pac.getLogin(), pac.getPasswd()),
                s3DataLake,
                s3Resources);
    }

    Producer(ResourceDescriptor rdesc, final IStamp stamper, final IStorage storage, final IStorage resources) throws StorageError {

        this(rdesc, stamper, storage, resources,
                Map.of(
                        "fac", new Pair<>((IDecodeStep) (InputStreamReader reader) -> {
                            try (reader) {
                                return new FacturaRequestDTO(reader);
                            } catch (IOException ex) {
                                log.error("Factura request DTO could not see the light");
                                throw new DecodeError("Factura request can not be decoded");
                            }
                        }, Wiring::fac),
                        "nom", new Pair<>((IDecodeStep) (InputStreamReader reader) -> {
                            try (reader) {
                                return new NominaRequestDTO(reader);
                            } catch (IOException ex) {
                                log.error("Nomina request DTO could not see the light");
                                throw new DecodeError("Nomina request can not be decoded");
                            }
                        }, Wiring::nom)));

    }

    Producer(ResourceDescriptor rdesc, final IStamp stamper, final IStorage storage, final IStorage resources, Map<String, Pair<IDecodeStep, IXmlStep>> scenarios) throws StorageError {
        super(stamper, storage, resources, scenarios);
        this.rdesc = rdesc;
    }

    @Override
    protected void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError {

        final String fileName = String.format("%s/%s.%s", st.getTargetName(), pacResult.getContent().getName(), XML_FILE_EXTENSION);
        byte[] in = pacResult.getContent().getBuffer().toString().getBytes(StandardCharsets.UTF_8);

        st.upload(XML_MIME_TYPE, in.length, fileName, new ByteArrayInputStream(in));
    }

    @Override
    protected String openPayload(final IPayload payload, Pickard pic) throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        S3ReqURLParser reqMeta = S3ReqURLParser.parse(payload.getReq());
        BufferedInputStream bf = this.getStorage().download(payload.getReq());
        InputStreamReader instreamReader = new InputStreamReader(bf, StandardCharsets.UTF_8);
        String[] parts = reqMeta.getParticles();
        Optional<ResourceDescriptor.Issuer> issuer = rdesc.getIssuer(parts[S3ReqURLParser.URIParticles.ISSUER.getIdx()]);
        return pic.route(
                parts[S3ReqURLParser.URIParticles.KIND.getIdx()],
                issuer.orElseThrow(() -> new RequestError("The issuer requested is not registered")).turnIntoMap(),
                instreamReader);
    }

    @Override
    protected BufferedInputStream fetchCert(IStorage resources, Map<String, String> issuerAttribs) throws StorageError {

        return ResourceFetchHelper.obtainCert(resources, issuerAttribs);
    }

    @Override
    protected BufferedInputStream fetchKey(IStorage resources, Map<String, String> issuerAttribs) throws StorageError {

        return ResourceFetchHelper.obtainKey(resources, issuerAttribs);
    }

    @Override
    protected String fetchPassword(Map<String, String> issuerAttribs) throws StorageError {

        Optional<String> passwd = Optional.ofNullable(issuerAttribs.get("passwd"));
        return passwd.orElseThrow(() -> new StorageError("The issuer requested is not having a password"));
    }

    private static class Wiring {

        public static <R extends Request> PacRes fac(R req, IStamp<PacRegularRequest, PacRes> stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String passwd) throws FormatError, StorageError {

            FacturaXml ic = new FacturaXml((FacturaRequestDTO) req, certificate, signerKey, passwd);
            return stamper.impress(new PacRegularRequest(ic.toString()));
        }

        public static <R extends Request> PacRes nom(R req, IStamp<PacRegularRequest, PacRes> stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String passwd) throws FormatError, StorageError {

            NominaXml ic = new NominaXml((NominaRequestDTO) req, certificate, signerKey, passwd);
            return stamper.impress(new PacRegularRequest(ic.toString()));
        }
    }
}
