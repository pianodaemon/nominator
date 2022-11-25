package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.javatuples.Pair;

public class Producer extends Pipeline {

    private static final String XML_FILE_EXTENSION = ".xml";

    public static Producer obtainSteadyPipeline() throws StorageError {

        return new Producer(PacRegularStamp.setupWithEnv());
    }

    public Producer(final IStamp stamper) throws StorageError {

        this(stamper,
                new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_DATA_LAKE")),
                new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_RESOURCES"))
        );
    }

    Producer(final IStamp stamper, final IStorage storage, final IStorage resources) throws StorageError {

        this(stamper, storage, resources,
                Map.of(
                        "fac", new Pair<>(reader -> new FacturaRequestDTO(reader), Wiring::fac),
                        "nom", new Pair<>(reader -> new NominaRequestDTO(reader), Wiring::nom)));
    }

    Producer(final IStamp stamper, final IStorage storage, final IStorage resources, Map<String, Pair<IDecodeStep, IXmlStep>> scenarios) throws StorageError {
        super(stamper, storage, resources, scenarios);
    }

    @Override
    protected void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError {

        final String fileName = String.format("%s/%s.%s", st.getTargetName(), pacResult.getContent().getName(), XML_FILE_EXTENSION);
        byte[] in = pacResult.getContent().getBuffer().toString().getBytes(StandardCharsets.UTF_8);

        st.upload("text/xml", in.length, fileName, new ByteArrayInputStream(in));
    }

    public static class Wiring {

        public static <R extends Request> PacRes fac(R req, IStamp<PacRegularRequest, PacRes> stamper) throws FormatError, StorageError {

            FacturaXml ic = new FacturaXml((FacturaRequestDTO) req);
            return stamper.impress(new PacRegularRequest(ic.toString()));
        }

        public static <R extends Request> PacRes nom(R req, IStamp<PacRegularRequest, PacRes> stamper) throws FormatError, StorageError {

            NominaXml ic = new NominaXml((NominaRequestDTO) req);
            return stamper.impress(new PacRegularRequest(ic.toString()));
        }
    }
}
