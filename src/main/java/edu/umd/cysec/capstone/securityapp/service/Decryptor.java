package edu.umd.cysec.capstone.securityapp.service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Decryptor {

    private Cipher cipher;

    public Decryptor(@Value("${secret.key}")String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher = Cipher.getInstance("AES");
        SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
        // encrypt the text
        cipher.init(Cipher.DECRYPT_MODE, originalKey);
    }
    public String decrypt(String encryptedText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(cipherText);
    }
}
