package com.luigivincent.pennappsxiv;

/**
 * Created by Luigi on 9/10/2016.
 */

/* Author: Luigi Vincent
* Applying standard AES Encryption / Decryption algorithm
*/

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    private final static String key = "Bar12345Bar12345"; // 128 bit key

    public static String decode(String input) {
        String result = null;
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            byte[] bb = new byte[input.length()];
            for (int i = 0; i < input.length(); i++) {
                bb[i] = (byte) input.charAt(i);
            }
            result = new String(cipher.doFinal(bb));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}