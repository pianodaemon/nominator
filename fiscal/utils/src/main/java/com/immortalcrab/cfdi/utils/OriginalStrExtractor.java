package com.immortalcrab.cfdi.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

class OriginalStrExtractor {

    public static String read(BufferedReader br, Source srcXslt) throws Exception {

        byte[] chunk = readBytes(br);
        return build(chunk, srcXslt);
    }

    private static byte[] readBytes(BufferedReader br) throws IOException {

        StringBuilder sb = new StringBuilder();
        String sCurrentLine;

        while ((sCurrentLine = br.readLine()) != null) {
            sb.append(sCurrentLine);
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8.name());
    }

    private static String build(byte[] chunk, Source srcXslt) throws Exception {

        InputStream is = new ByteArrayInputStream(chunk);
        final Source srcXml = new StreamSource(is);
        StringWriter outStr = new StringWriter();
        Result res = new StreamResult(outStr);

        TransformerFactory trFactory = TransformerFactory.newInstance();
        Transformer tr = trFactory.newTransformer(srcXslt);
        tr.transform(srcXml, res);

        return outStr.toString();
    }
}
