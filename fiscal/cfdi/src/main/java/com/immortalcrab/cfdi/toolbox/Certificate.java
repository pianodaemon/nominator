package com.immortalcrab.cfdi.toolbox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

class Certificate {

    public static String read(byte[] chunk) throws UnsupportedEncodingException, IOException {

        return new String(
                Base64.encodeBase64(chunk),
                StandardCharsets.UTF_8.name()
        );
    }
}
