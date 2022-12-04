package com.immortalcrab.cfdi.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Source;

public interface IToolbox {

    public String renderCertificate(byte[] chunk) throws UnsupportedEncodingException, IOException;

    public String renderOriginalStr(BufferedReader br, Source srcXslt) throws Exception;

    public String signOriginalStr(BufferedReader brPrivKeyPem, String original) throws Exception;
}
