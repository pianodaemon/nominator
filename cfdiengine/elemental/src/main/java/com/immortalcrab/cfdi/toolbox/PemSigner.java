package com.immortalcrab.cfdi.toolbox;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

class PemSigner {

    private static final String EMSG = "Pem signer went mad";

    public static String sign(BufferedReader brPrivKeyPem, String msg) throws EngineError {
        Signature sign;
        try {
            sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(extractRsaPrivateKey(brPrivKeyPem));
            sign.update(msg.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
            throw new EngineError(EMSG, ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }
    }

    private static RSAPrivateKey extractRsaPrivateKey(BufferedReader br) throws EngineError {
        try {
            return getPrivateKeyFromString(getKey(br));
        } catch (IOException | GeneralSecurityException ex) {
            throw new EngineError(EMSG, ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }
    }

    private static String getKey(BufferedReader br) throws EngineError {
        String strKeyPEM = "";
        try (br) {
            String line;
            while ((line = br.readLine()) != null) {
                strKeyPEM += line + "\n";
            }
            return strKeyPEM;
        } catch (IOException ex) {
            throw new EngineError(EMSG, ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }
    }

    private static RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }
}
