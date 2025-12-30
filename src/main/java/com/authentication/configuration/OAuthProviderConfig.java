package com.authentication.configuration;

import com.authentication.enums.Provider;
import com.authentication.oauth2.OAuthProviderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class OAuthProviderConfig {

    @Bean
    public Map<Provider, OAuthProviderClient> oAuthProviderMap(
            List<OAuthProviderClient> clients
    ) {
        return clients.stream()
                .collect(Collectors.toMap(
                        OAuthProviderClient::provider,
                        Function.identity()
                ));
    }
}
