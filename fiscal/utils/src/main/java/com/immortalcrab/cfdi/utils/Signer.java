package com.immortalcrab.cfdi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Signer {

    public static String sign(BufferedReader brPrivKeyPem, String msg)
            throws IOException, GeneralSecurityException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {

        RSAPrivateKey pkey = getPrivateKey(brPrivKeyPem);

        return signMsg(pkey, msg);
    }

    private static RSAPrivateKey getPrivateKey(BufferedReader br) throws IOException, GeneralSecurityException {

        String privateKeyPEM = getKey(br);

        return getPrivateKeyFromString(privateKeyPEM);
    }

    private static String signMsg(PrivateKey privateKey, String message)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8.name()));

        return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8.name());
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

    private static RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        return privKey;
    }
}
