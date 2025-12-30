package com.authentication.oauth2;

import com.authentication.dto.OAuthUser;
import com.authentication.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

//GET https://graph.facebook.com/v18.0/oauth/access_token

@Component
@RequiredArgsConstructor
public class FacebookOAuthClient implements OAuthProviderClient {

    private final OAuth2Properties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Provider provider() {
        return Provider.FACEBOOK;
    }

    @Override
    public OAuthToken exchangeCodeForToken(String code) {
        var client = properties.getClients().get("facebook");
        var provider = properties.getProviders().get("facebook");

        String tokenUrl = UriComponentsBuilder
                .fromUriString(provider.getTokenUri())
                .queryParam("client_id", client.getClientId())
                .queryParam("client_secret", client.getClientSecret())
                .queryParam("redirect_uri", client.getRedirectUri())
                .queryParam("code", code)
                .toUriString();

        ResponseEntity<Map> response =
                restTemplate.getForEntity(tokenUrl, Map.class);

        Map<String, Object> res = response.getBody();
        if (res == null || !res.containsKey("access_token")) {
            throw new IllegalStateException("Facebook token exchange failed");
        }

        return new OAuthToken(
                (String) res.get("access_token"),
                null,
                res.get("expires_in") != null
                        ? ((Number) res.get("expires_in")).longValue()
                        : null,
                "Bearer",
                null
        );
    }

    //GET https://graph.facebook.com/me?fields=id,name,email,picture&access_token=ACCESS_TOKEN
    @Override
    public OAuthUser fetchUser(String accessToken) {

        String userInfoUrl = UriComponentsBuilder
                .fromUriString(properties.getProviders().get("facebook").getUserInfoUri())
                .queryParam("access_token", accessToken)
                .toUriString();

        ResponseEntity<Map> response =
                restTemplate.getForEntity(userInfoUrl, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Facebook userinfo failed");
        }

        Map<String, Object> picture = (Map<String, Object>) body.get("picture");
        Map<String, Object> data = picture != null
                ? (Map<String, Object>) picture.get("data")
                : null;

        return new OAuthUser(
                "facebook",
                (String) body.get("id"),
                (String) body.get("email"),
                (String) body.get("name"),
                data != null ? (String) data.get("url") : null
        );
    }
}
