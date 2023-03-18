package edu.umd.cysec.capstone.securityapp.service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Decryptor extends CrytoBase {


    public Decryptor(@Value("${secret.key}")String key) {
         this.password = key;
    }


    public String decryptString(String base64CiphertextAndNonceAndSalt) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        // Decode the base64.
        byte[] ciphertextAndNonceAndSalt = Base64.getDecoder().decode(base64CiphertextAndNonceAndSalt);

        // Retrieve the salt and ciphertextAndNonce.
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        byte[] ciphertextAndNonce = new byte[ciphertextAndNonceAndSalt.length - PBKDF2_SALT_SIZE];
        System.arraycopy(ciphertextAndNonceAndSalt, 0, salt, 0, salt.length);
        System.arraycopy(ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce, 0, ciphertextAndNonce.length);

        // Create an instance of PBKDF2 and derive the key.
        PBEKeySpec pwSpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
        byte[] key = keyFactory.generateSecret(pwSpec).getEncoded();

        // Decrypt and return result.
        return new String(decrypt(ciphertextAndNonce, key), StandardCharsets.UTF_8);
    }

    private byte[] decrypt(byte[] ciphertextAndNonce, byte[] key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        // Retrieve the nonce and ciphertext.
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        byte[] ciphertext = new byte[ciphertextAndNonce.length - ALGORITHM_NONCE_SIZE];
        System.arraycopy(ciphertextAndNonce, 0, nonce, 0, nonce.length);
        System.arraycopy(ciphertextAndNonce, nonce.length, ciphertext, 0, ciphertext.length);

        // Create the cipher instance and initialize.
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));

        // Decrypt and return result.
        return cipher.doFinal(ciphertext);
    }
}
