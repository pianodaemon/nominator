package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.javatuples.Pair;

public class Producer extends Pipeline implements IIssuer {

    public Producer() throws StorageError {

        this(PacRegularStamp.setupWithEnv());
    }

    public Producer(final IStamp stamper) throws StorageError {

        this(stamper,
                new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_PERSISTANCE_TARGET")));
    }

    Producer(final IStamp stamper, final IStorage storage) throws StorageError {

        this(
                stamper,
                storage,
                Map.of(
                        "fac", new Pair<>(reader -> new FacturaRequestDTO(reader), FacturaXml::render),
                        "nom", new Pair<>(reader -> new NominaRequestDTO(reader), NominaXml::render))
        );
    }

    Producer(final IStamp stamper, final IStorage storage, Map<String, Pair<IDecodeStep, IXmlStep>> scenarios) throws StorageError {
        super(stamper, storage, scenarios);
    }

    @Override
    public String doIssue(final String kind, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        return this.issue(kind, isr);
    }

    @Override
    protected void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError {

        final String fileName = String.format("%s/%s.%s", st.getTargetName(), pacResult.getContent().getName(), ".xml");
        byte[] in = pacResult.getContent().getBuffer().toString().getBytes(StandardCharsets.UTF_8);

        st.upload("text/xml", in.length, fileName, new ByteArrayInputStream(in));
    }
}
