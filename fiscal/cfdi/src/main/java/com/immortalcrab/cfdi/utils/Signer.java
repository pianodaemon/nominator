package com.immortalcrab.cfdi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    public static String signMessage(String privKeyPemFilename, String message)
            throws IOException, GeneralSecurityException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {

        RSAPrivateKey pkey = getPrivateKey(privKeyPemFilename);

        return sign(pkey, message);
    }

    private static RSAPrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {

        String privateKeyPEM = getKey(filename);

        return getPrivateKeyFromString(privateKeyPEM);
    }

    private static String sign(PrivateKey privateKey, String message)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes("UTF-8"));

        return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
    }

    private static String getKey(String filename) throws IOException {
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
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
