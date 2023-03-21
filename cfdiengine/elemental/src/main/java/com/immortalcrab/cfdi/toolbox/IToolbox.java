package com.immortalcrab.cfdi.toolbox;

import com.immortalcrab.cfdi.errors.EngineError;

import java.io.BufferedReader;
import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

public interface IToolbox {

    public default String renderCerticate(byte[] chunk) throws EngineError {
        return new String(Base64.encodeBase64(chunk), StandardCharsets.UTF_8);
    }

    public default String renderOriginal(BufferedReader br, Source srcXslt) throws EngineError {
        return OriginalStrExtractor.read(br, srcXslt);
    }

    public default String signOriginal(BufferedReader brPrivKeyPem, String original) throws EngineError {
        return PemSigner.sign(brPrivKeyPem, original);
    }
}
