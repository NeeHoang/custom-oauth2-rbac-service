package com.authentication.service.impl;

import com.authentication.dto.OAuthUser;
import com.authentication.dto.response.AuthResponse;
import com.authentication.entity.Role;
import com.authentication.entity.User;
import com.authentication.enums.Provider;
import com.authentication.mapstruct.OAuthUserMapper;
import com.authentication.mapstruct.UserResponseMapper;
import com.authentication.oauth2.InMemoryOAuthStateStore;
import com.authentication.oauth2.OAuth2Properties;
import com.authentication.oauth2.OAuthProviderClient;
import com.authentication.oauth2.OAuthToken;
import com.authentication.redis.model.RefreshTokenData;
import com.authentication.redis.model.RefreshTokenGenerator;
import com.authentication.repository.RoleRepository;
import com.authentication.repository.UserRepository;
import com.authentication.service.IOAuth2Service;
import com.authentication.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService implements IOAuth2Service {

    private final OAuth2Properties properties;
    private final InMemoryOAuthStateStore stateStore;
    private final Map<Provider, OAuthProviderClient> providerMap;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void debug() {
        log.info("OAuth2 clients: {}", properties.getClients().keySet());
        log.info("OAuth2 providers: {}", properties.getProviders().keySet());
    }

    @Override
    public String buildAuthorizeUrl( String providerName) {

        OAuth2Properties.Client client = properties.getClients().get(providerName);
        OAuth2Properties.ProviderConfig providerConfig = properties.getProviders().get(providerName);

        if (client == null || providerConfig == null) {
            throw new IllegalArgumentException("Unsupported provider" + providerName);
        }

        String state = UUID.randomUUID().toString();
        log.info("state: {}", state);
        stateStore.store(state);

        return UriComponentsBuilder
                .fromUriString(providerConfig.getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", client.getClientId())
                .queryParam("redirect_uri", client.getRedirectUri())
                .queryParam("scope", String.join(" ", client.getScope()))
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    @Override
    public AuthResponse handleCallback(String providerName, String code) {

        Provider provider = Provider.valueOf(providerName.toUpperCase());
        OAuthProviderClient client = providerMap.get(provider);

        if (client == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + providerName);
        }

        OAuthToken token = client.exchangeCodeForToken(code);
        OAuthUser oAuthUser = client.fetchUser(token.accessToken());
        log.info("Access token: {}", token.accessToken());

        // OAuthUser → User (DB)
        User user = createUserFromOAuth(oAuthUser);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Issue JWT
        String accessToken = jwtService.generateToken(user.getId(), user.getRoles());

        // Refresh Token
        String refreshToken = RefreshTokenGenerator.generate();
        redisTemplate.opsForValue().set(
                "refresh:" + refreshToken,
                new RefreshTokenData(user.getId(), "default device-id"),
                Duration.ofDays(30)
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponseMapper.toResponse(user))
                .build();
    }

    @Override
    public User createUserFromOAuth(OAuthUser oAuthUser) {
        return userRepository.findByEmail(oAuthUser.email())
                .orElseGet(() -> {
                    User user = OAuthUserMapper.toUser(oAuthUser);

                    Role staffRole = roleRepository.findByName("ROLE_STAFF")
                            .orElseThrow(() ->
                                    new IllegalStateException("ROLE_STAFF not found"));

                    user.setRoles(Set.of(staffRole));
                    user.setCreatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                });
    }
}
