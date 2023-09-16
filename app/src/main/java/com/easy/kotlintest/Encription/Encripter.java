package com.easy.kotlintest.Encription;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encripter {

    private static final String ALGORITHM = "Blowfish";
    private static final String MODE = "Blowfish/CBC/PKCS5Padding";
    private static final String IV = "abcdefgh";
    public static String key = "";

    public Encripter(String key) {
        Encripter.key = key;
    }

    public  String encrypt(String value) throws Exception {

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] values = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(values, Base64.DEFAULT);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public  String decrepit(String value) {

        try {
            byte[] values = Base64.decode(value, Base64.DEFAULT);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));

            return new String(cipher.doFinal(values));


        } catch (Exception e) {
            return value;
        }

    }

}
