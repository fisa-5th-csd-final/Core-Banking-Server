package com.fisa.bank.common.config.security.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    public static PrivateKey createPrivateKey(String string, String algorithm){
        try {
            byte[] pkcs8 = Base64.getDecoder().decode(string);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
            return KeyFactory.getInstance(algorithm).generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new IllegalStateException("Failed to create private key", e);
        }
    }

    public static PublicKey createPublicKey(String string, String algorithm){
        try {
            byte[] pkcs8 = Base64.getDecoder().decode(string);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
            return KeyFactory.getInstance(algorithm).generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new IllegalStateException("Failed to create private key", e);
        }
    }

}
