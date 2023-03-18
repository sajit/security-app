package edu.umd.cysec.capstone.securityapp.service;

public abstract class CrytoBase {

    protected static final String ALGORITHM_NAME = "AES/GCM/NoPadding";
    protected static final int ALGORITHM_NONCE_SIZE = 12;
    protected static final int ALGORITHM_TAG_SIZE = 128;
    protected static final int ALGORITHM_KEY_SIZE = 128;
    protected static final String PBKDF2_NAME = "PBKDF2WithHmacSHA256";
    protected static final int PBKDF2_SALT_SIZE = 16;
    protected static final int PBKDF2_ITERATIONS = 32767;

    protected String password;

}
