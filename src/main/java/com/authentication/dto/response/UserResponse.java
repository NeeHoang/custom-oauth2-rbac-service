package com.authentication.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phone;
    private Boolean isVerified;
    private String status;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Set<String> roles;
}
