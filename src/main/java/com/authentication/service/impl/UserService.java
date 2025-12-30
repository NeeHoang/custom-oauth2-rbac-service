package com.authentication.service.impl;

import com.authentication.dto.request.LoginRequest;
import com.authentication.dto.request.RegisterRequest;
import com.authentication.dto.response.AuthResponse;
import com.authentication.dto.response.UserResponse;
import com.authentication.entity.Role;
import com.authentication.entity.User;
import com.authentication.redis.model.RefreshTokenData;
import com.authentication.redis.model.RefreshTokenGenerator;
import com.authentication.repository.RoleRepository;
import com.authentication.repository.UserRepository;
import com.authentication.service.IUserService;
import com.authentication.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public AuthResponse authLogin(LoginRequest request, String deviceId) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Account not exist"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Password incorrect");
        }

        // update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getId(), user.getRoles());

        String refreshToken = RefreshTokenGenerator.generate();
        redisTemplate.opsForValue().set(
                "refresh:" + refreshToken,
                new RefreshTokenData(user.getId(), deviceId),
                Duration.ofDays(30)
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse authRegistration(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exist");

        Role roleStaff = roleRepository.findByName("ROLE_STAFF")
                .orElseThrow(() -> new IllegalStateException("ROLE_STAFF not found"));

        User user = User.builder()
                .username(request.getUserName())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassWord()))
                .roles(Set.of(roleStaff))
                .lastLogin(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getId(), user.getRoles());

        return AuthResponse.builder()
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .isVerified(user.getIsVerified())
                .status(String.valueOf(user.getStatus()))
                .provider(String.valueOf(user.getProvider()))
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
