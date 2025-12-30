package com.authentication.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {

    private Map<String, Client> clients = new HashMap<>();
    private Map<String, ProviderConfig> providers = new HashMap<>();

    @Getter
    @Setter
    public static class Client {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> scope;
    }

    @Getter
    @Setter
    public static class ProviderConfig {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
    }
}
