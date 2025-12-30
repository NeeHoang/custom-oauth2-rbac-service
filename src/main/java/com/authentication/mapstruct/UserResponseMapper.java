package com.authentication.mapstruct;

import com.authentication.dto.response.UserResponse;
import com.authentication.entity.Role;
import com.authentication.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserResponseMapper {

    private UserResponseMapper() {}

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .isVerified(user.getIsVerified())
                .status(String.valueOf(user.getStatus()))
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .roles(mapRoles(user.getRoles()))
                .build();
    }

    private static Set<String> mapRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
