package com.authentication.service;

import com.authentication.dto.OAuthUser;
import com.authentication.dto.response.AuthResponse;
import com.authentication.entity.User;

public interface IOAuth2Service {

    String buildAuthorizeUrl(String providerName);

    AuthResponse handleCallback(String providerName, String code);

    User createUserFromOAuth(OAuthUser oAuthUser);
}
