package com.authentication.oauth2;

import com.authentication.dto.OAuthUser;
import com.authentication.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

//Content-Type: application/x-www-form-urlencoded
//Accept: application/json

@Component
@RequiredArgsConstructor
public class GithubOAuthClient implements OAuthProviderClient {

    private final OAuth2Properties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Provider provider() {
        return Provider.GITHUB;
    }

    @Override
    public OAuthToken exchangeCodeForToken(String code) {
        var client = properties.getClients().get("github");
        var provider = properties.getProviders().get("github");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", client.getClientId());
        body.add("client_secret", client.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", client.getRedirectUri());

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        provider.getTokenUri(),
                        entity,
                        Map.class
                );
        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("access_token")) {
            throw new IllegalStateException("Github token exchange failed");
        }

        return new OAuthToken(
                (String) res.get("access_token"),
                null,
                null,
                (String) res.get("token_type"),
                (String) res.get("scope")
        );
    }

    //GET https://api.github.com/user
    //Authorization: Bearer ACCESS_TOKEN
    //GET https://api.github.com/user/emails
    @Override
    public OAuthUser fetchUser(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 1. Profile
        ResponseEntity<Map> profileRes = restTemplate.exchange(
                properties.getProviders().get("github").getUserInfoUri(),
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> profile = profileRes.getBody();
        if (profile == null) {
            throw new IllegalStateException("GitHub profile fetch failed");
        }

        String email = (String) profile.get("email");

        // 2. Email fallback
        if (email == null) {
            ResponseEntity<List> emailsRes = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<Map<String, Object>> emails = emailsRes.getBody();
            if (emails != null) {
                email = emails.stream()
                        .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                        .map(e -> (String) e.get("email"))
                        .findFirst()
                        .orElse(null);
            }
        }

        return new OAuthUser(
                "github",
                String.valueOf(profile.get("id")),
                email,
                (String) profile.get("name"),
                (String) profile.get("avatar_url")
        );
    }
}
