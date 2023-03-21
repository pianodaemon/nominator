package com.immortalcrab.cfdi.toolbox;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

class OriginalStrExtractor {

    private static final String EMSG = "Original string extractor went mad";

    public static String read(BufferedReader br, Source srcXslt) throws EngineError {

        byte[] chunk = readBytes(br);
        return build(chunk, srcXslt);
    }

    private static byte[] readBytes(BufferedReader br) throws EngineError {

        StringBuilder sb = new StringBuilder();
        String sCurrentLine;

        try {
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
        } catch (IOException ex) {
            throw new EngineError(EMSG, ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String build(byte[] chunk, Source srcXslt) throws EngineError {

        InputStream is = new ByteArrayInputStream(chunk);
        final Source srcXml = new StreamSource(is);
        StringWriter outStr = new StringWriter();
        Result res = new StreamResult(outStr);

        TransformerFactory trFactory = TransformerFactory.newInstance();
        try {
            Transformer tr = trFactory.newTransformer(srcXslt);
            tr.transform(srcXml, res);
            return outStr.toString();
        } catch (TransformerException ex) {
            throw new EngineError(EMSG, ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }
    }
}
