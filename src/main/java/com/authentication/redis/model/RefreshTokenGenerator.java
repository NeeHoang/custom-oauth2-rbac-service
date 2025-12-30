package com.authentication.redis.model;

import java.security.SecureRandom;
import java.util.Base64;

public class RefreshTokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        byte[] bytes = new byte[64];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
