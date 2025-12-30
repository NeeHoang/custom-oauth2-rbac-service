package com.authentication.service;

import com.authentication.dto.response.AuthResponse;

public interface IRefreshTokenService {
    AuthResponse refresh(String refreshToken, String deviceId);
    void logout(String refreshToken, String deviceId);
}
