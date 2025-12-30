package com.authentication.service;

import com.authentication.dto.request.LoginRequest;
import com.authentication.dto.request.RegisterRequest;
import com.authentication.dto.response.AuthResponse;

public interface IUserService {
    AuthResponse authLogin(LoginRequest request, String deviceId);

    AuthResponse authRegistration(RegisterRequest request);
}
