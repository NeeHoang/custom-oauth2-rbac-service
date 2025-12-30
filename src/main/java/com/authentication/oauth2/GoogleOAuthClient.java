package com.authentication.oauth2;

import com.authentication.dto.OAuthUser;
import com.authentication.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthProviderClient {

    private final OAuth2Properties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Provider provider() {
        return Provider.GOOGLE;
    }

    //POST https://oauth2.googleapis.com/token
    //Content-Type: application/x-www-form-urlencoded
    @Override
    public OAuthToken exchangeCodeForToken(String code) {

        var client = properties.getClients().get("google");
        var provider = properties.getProviders().get("google");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("client_id", client.getClientId());
        body.add("client_secret", client.getClientSecret());
        body.add("redirect_uri", client.getRedirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                provider.getTokenUri(),
                request,
                Map.class
        );

        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("access_token")) {
            throw new IllegalStateException("Google token exchange failed");
        }

        return new OAuthToken(
                (String) res.get("access_token"),
                (String) res.get("refresh_token"),
                res.get("expires_in") != null
                        ? ((Number) res.get("expires_in")).longValue()
                        : null,
                (String) res.get("token_type"),
                (String) res.get("scope")
        );
    }

    //GET https://openidconnect.googleapis.com/v1/userinfo
    //Authorization: Bearer ACCESS_TOKEN
    @Override
    public OAuthUser fetchUser(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                properties.getProviders().get("google").getUserInfoUri(),
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Google userinfo failed");
        }

        return new OAuthUser(
                "google",
                (String) body.get("sub"),
                (String) body.get("email"),
                (String) body.get("name"),
                (String) body.get("picture")
        );
    }
}
