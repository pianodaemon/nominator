package com.immortalcrab.cfdi.pipeline;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.InputStreamReader;
import org.javatuples.Pair;

public class Producer extends Pipeline implements IIssuer {

    public Producer() throws StorageError {

        this(PacRegularStamp.setupWithEnv(),
                new S3BucketStorage(S3ClientHelper.setupWithEnv(), System.getenv("BUCKET_TARGET")));
    }

    Producer(final IStamp stamper, final IStorage storage) throws StorageError {

        this(
                stamper,
                storage,
                ImmutableMap.of(
                        "fac", new Pair<>(reader -> new FacturaRequestDTO(reader), FacturaXml::render),
                        "nom", new Pair<>(reader-> new NominaRequestDTO(reader), NominaXml::render))
        );
    }

    Producer(final IStamp stamper, final IStorage storage, ImmutableMap<String, Pair<IDecodeStep, IXmlStep>> scenarios) throws StorageError {
        super(stamper, storage, scenarios);
    }

    @Override
    public String doIssue(final String kind, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        return this.issue(kind, isr);
    }
}
