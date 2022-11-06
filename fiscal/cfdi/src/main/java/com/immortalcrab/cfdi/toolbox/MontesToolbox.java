package com.immortalcrab.cfdi.toolbox;

import com.immortalcrab.cfdi.toolbox.IToolbox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Source;

public class MontesToolbox implements IToolbox {

    @Override
    public String renderCerticate(byte[] chunk) throws UnsupportedEncodingException, IOException {
        return Certificate.read(chunk);
    }

    @Override
    public String renderOriginal(BufferedReader br, Source srcXslt) throws Exception {
        return OriginalStrExtractor.read(br, srcXslt);
    }

    @Override
    public String signOriginal(BufferedReader brPrivKeyPem, String original) throws Exception {
        return Signer.sign(brPrivKeyPem, original);
    }
}
