package com.authentication.oauth2;

public record OAuthToken(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        String scope
) {}
