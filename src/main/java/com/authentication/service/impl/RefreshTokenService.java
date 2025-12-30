package com.authentication.service.impl;

import com.authentication.dto.response.AuthResponse;
import com.authentication.entity.User;
import com.authentication.redis.model.RefreshTokenData;
import com.authentication.redis.model.RefreshTokenGenerator;
import com.authentication.repository.UserRepository;
import com.authentication.service.IRefreshTokenService;
import com.authentication.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private static final String PREFIX = "refresh:";
    private static final Duration REFRESH_TTL = Duration.ofDays(30);

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public AuthResponse refresh(String refreshToken, String deviceId) {

        String key = PREFIX + refreshToken;

        RefreshTokenData data = (RefreshTokenData) redisTemplate
                .opsForValue().get(key);

        if (data == null)
            throw new RuntimeException("Invalid refresh token");
        if (!data.getDeviceId().equals(deviceId))
            throw new RuntimeException("Device mismatch");

        // rotate: delet old token
        redisTemplate.delete(key);

        // create new token
        String newRefreshToken = RefreshTokenGenerator.generate();

        redisTemplate.opsForValue().set(
                PREFIX + newRefreshToken,
                new RefreshTokenData(data.getUserId(), deviceId),
                REFRESH_TTL
        );

        User user = userRepository.findById(data.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user.getId(), user.getRoles());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken, String deviceId) {
        String key = PREFIX + refreshToken;
        RefreshTokenData data = (RefreshTokenData) redisTemplate.opsForValue().get(key);

        if (data == null)
            return;
        if (!data.getDeviceId().equals(deviceId))
            throw new RuntimeException("Device mismatch");

        redisTemplate.delete(key);
    }
}
