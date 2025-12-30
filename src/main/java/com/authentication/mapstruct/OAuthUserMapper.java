package com.authentication.mapstruct;

import com.authentication.dto.OAuthUser;
import com.authentication.entity.User;
import com.authentication.enums.Provider;
import com.authentication.enums.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

public class OAuthUserMapper {

    public static User toUser(OAuthUser oauthUser) {

        String rawPassword = UUID.randomUUID().toString();
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        return User.builder()
                .username(buildUsername(oauthUser))
                .email(oauthUser.email())
                .fullName(oauthUser.name())
                .avatarUrl(oauthUser.avatar())
                .passwordHash(hashedPassword) // hash BCrypt UUID random
                .provider(Provider.valueOf(oauthUser.provider().toUpperCase()))
                .isVerified(true) // OAuth coi như verified
                .status(UserStatus.valueOf("ACTIVE"))
                .build();
    }

    private static String buildUsername(OAuthUser oauthUser) {
        return oauthUser.provider() + "_" + oauthUser.providerId();
    }
}
