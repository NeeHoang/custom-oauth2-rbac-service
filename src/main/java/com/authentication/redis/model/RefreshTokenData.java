package com.authentication.redis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class RefreshTokenData {

    private Long userId;
    private String deviceId;
    private Instant issuedAt;

    public RefreshTokenData(Long userId, String deviceId) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.issuedAt = Instant.now();
    }
}
