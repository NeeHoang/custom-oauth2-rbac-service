package com.authentication.controller;

import com.authentication.dto.request.LoginRequest;
import com.authentication.dto.request.RegisterRequest;
import com.authentication.dto.response.AuthResponse;
import com.authentication.service.IRefreshTokenService;
import com.authentication.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final IRefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            @RequestHeader("X-Device-Id") String deviceId) {

        if (deviceId == null || deviceId.isBlank())
           deviceId = "No device-id";

        log.info("Login with email : {}", request.getEmail());
        AuthResponse response = userService.authLogin(request, deviceId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("X-Refresh-Token") String refreshToken,
            @RequestHeader("X-Device-Id") String deviceId
    ) {
        if (refreshToken == null || deviceId == null)
            deviceId = "No device-id";
        refreshTokenService.logout(refreshToken, deviceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registration(@Valid @RequestBody RegisterRequest request) {

        log.info("Sign up with email : {}", request.getEmail());
        AuthResponse response = userService.authRegistration(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken,
            @RequestHeader("X-Device-Id") String deviceId
    ) {
        if (refreshToken == null || deviceId == null)
            deviceId = "No device-id";

        AuthResponse response = refreshTokenService.refresh(
                refreshToken,
                deviceId
        );
        return ResponseEntity.ok(response);
    }

}
