package com.authentication.dto;

public record OAuthLoginRequest(
        String provider,
        String code,
        String redirectUri
) {}
