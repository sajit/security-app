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
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Encryptor extends  CrytoBase{




    public Encryptor(@Value("${secret.key}")String key)  {
        this.password = key;
    }


    public String encryptString(String plaintext) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException{
        // Generate a 128-bit salt using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        rand.nextBytes(salt);

        // Create an instance of PBKDF2 and derive a key.
        PBEKeySpec pwSpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
        byte[] key = keyFactory.generateSecret(pwSpec).getEncoded();

        // Encrypt and prepend salt.
        byte[] ciphertextAndNonce = encrypt(plaintext.getBytes(StandardCharsets.UTF_8), key);
        byte[] ciphertextAndNonceAndSalt = new byte[salt.length + ciphertextAndNonce.length];
        System.arraycopy(salt, 0, ciphertextAndNonceAndSalt, 0, salt.length);
        System.arraycopy(ciphertextAndNonce, 0, ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce.length);

        // Return as base64 string.
        return Base64.getEncoder().encodeToString(ciphertextAndNonceAndSalt);
    }

    private byte[] encrypt(byte[] plaintext, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        // Generate a 96-bit nonce using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        rand.nextBytes(nonce);

        // Create the cipher instance and initialize.
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));

        // Encrypt and prepend nonce.
        byte[] ciphertext = cipher.doFinal(plaintext);
        byte[] ciphertextAndNonce = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, ciphertextAndNonce, 0, nonce.length);
        System.arraycopy(ciphertext, 0, ciphertextAndNonce, nonce.length, ciphertext.length);

        return ciphertextAndNonce;
    }

}
