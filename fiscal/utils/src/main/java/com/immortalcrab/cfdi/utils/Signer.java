package com.immortalcrab.cfdi.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

public class Signer {

    public static String sign(BufferedReader brPrivKeyPem, String msg)
            throws IOException, GeneralSecurityException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {

        PrivateKey pkey = getPrivateKey(brPrivKeyPem);

        return signMsg(pkey, msg);
    }

    private static PrivateKey getPrivateKey(BufferedReader br) throws IOException, GeneralSecurityException {

        String privateKeyPEM = getKey(br);

        return getPrivateKeyFromString(privateKeyPEM);
    }

    private static String signMsg(PrivateKey privateKey, String message)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8));

        return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8);
    }

    private static String getKey(BufferedReader br) throws IOException {
        String strKeyPEM = "";
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();

        return strKeyPEM;
    }

    private static PrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return kf.generatePrivate(keySpec);
    }
}
