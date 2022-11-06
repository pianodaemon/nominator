package com.immortalcrab.cfdi.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Source;

public interface IToolbox {

    public String renderCerticate(byte[] chunk) throws UnsupportedEncodingException, IOException;

    public String renderOriginal(BufferedReader br, Source srcXslt) throws Exception;

    public String signOriginal(BufferedReader brPrivKeyPem, String original) throws Exception;
}
