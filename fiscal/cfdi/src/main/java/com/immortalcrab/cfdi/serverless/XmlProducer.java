package com.immortalcrab.cfdi.serverless;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.pipeline.Pipeline;
import com.immortalcrab.cfdi.pipeline.FacturaRequest;
import com.immortalcrab.cfdi.pipeline.FacturaXml;
import com.immortalcrab.cfdi.pipeline.IIssuer;
import com.immortalcrab.cfdi.pipeline.NominaRequest;
import com.immortalcrab.cfdi.pipeline.NominaXml;
import com.immortalcrab.cfdi.pipeline.S3BucketStorage;
import java.io.InputStreamReader;
import org.javatuples.Pair;

public XmlProducer extends Pipeline implements IIssuer {

    public XmlProducer() throws StorageError {

        super(
                S3BucketStorage.setupWithEnv(),
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
