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

        super(
                PacRegularStamp.setupWithEnv(),
                new S3BucketStorage(S3ClientHelper.setupWithEnv()),
                ImmutableMap.of(
                        "fac", new Pair<>(FacturaRequest::render, FacturaXml::render),
                        "nom", new Pair<>(NominaRequest::render, NominaXml::render))
        );
    }

    @Override
    public String doIssue(final String kind, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        return this.issue(kind, isr);
    }
}
