package com.authentication.oauth2;

import com.authentication.dto.OAuthUser;
import com.authentication.enums.Provider;

public interface OAuthProviderClient {
    Provider provider();

    OAuthToken exchangeCodeForToken(String code);

    OAuthUser fetchUser(String accessToken);
}
