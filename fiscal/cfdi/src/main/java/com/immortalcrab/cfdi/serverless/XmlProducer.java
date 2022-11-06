package com.immortalcrab.cfdi.serverless;

import com.google.common.collect.ImmutableMap;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.pipeline.Pipeline;
import com.immortalcrab.cfdi.pipeline.lola.FacturaRequest;
import com.immortalcrab.cfdi.pipeline.lola.FacturaXml;
import com.immortalcrab.cfdi.pipeline.lola.NominaRequest;
import com.immortalcrab.cfdi.pipeline.lola.NominaXml;
import com.immortalcrab.cfdi.pipeline.lola.S3BucketStorage;
import org.javatuples.Pair;

public class XmlProducer extends Pipeline {

    public XmlProducer() throws StorageError {

        super(
                S3BucketStorage.setupWithEnv(),
                ImmutableMap.of(
                        "fac", new Pair<>(FacturaRequest::render, FacturaXml::render),
                        "nom", new Pair<>(NominaRequest::render, NominaXml::render))
        );
    }
}
