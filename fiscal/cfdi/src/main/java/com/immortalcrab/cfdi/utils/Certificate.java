package com.immortalcrab.cfdi.utils;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

public class Certificate {

    public static String read(File file) throws UnsupportedEncodingException, IOException {

        byte[] bs = Files.readAllBytes(file.toPath());
        return new String(Base64.encodeBase64(bs), StandardCharsets.UTF_8.name());
    }
}
