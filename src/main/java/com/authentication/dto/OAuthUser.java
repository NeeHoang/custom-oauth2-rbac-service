package com.authentication.dto;

public record OAuthUser(
        String provider,
        String providerId,
        String email,
        String name,
        String avatar
) {}
