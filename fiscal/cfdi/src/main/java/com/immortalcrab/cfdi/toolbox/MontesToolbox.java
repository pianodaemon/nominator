package com.immortalcrab.cfdi.toolbox;

import com.immortalcrab.cfdi.utils.Certificate;
import com.immortalcrab.cfdi.utils.OriginalStrExtractor;
import com.immortalcrab.cfdi.utils.Signer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Source;

public class MontesToolbox implements IToolbox {

    @Override
    public String renderCertificate(byte[] chunk) throws UnsupportedEncodingException, IOException {
        return Certificate.read(chunk);
    }

    @Override
    public String renderOriginalStr(BufferedReader br, Source srcXslt) throws Exception {
        return OriginalStrExtractor.read(br, srcXslt);
    }

    @Override
    public String signOriginalStr(BufferedReader brPrivKeyPem, String original) throws Exception {
        return Signer.sign(brPrivKeyPem, original);
    }
}
