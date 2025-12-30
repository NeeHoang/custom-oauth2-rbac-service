package com.authentication.controller;

import com.authentication.dto.response.AuthResponse;
import com.authentication.oauth2.InMemoryOAuthStateStore;
import com.authentication.service.IOAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/oauth2")
public class OAuthController {

    private final IOAuth2Service oAuthService;
    private final InMemoryOAuthStateStore stateStore;

    @GetMapping("/authorize/{provider}")
    public ResponseEntity<Void> authorize(@PathVariable String provider) {

        String authorizeUrl = oAuthService.buildAuthorizeUrl(provider);

        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, authorizeUrl)
                .build();
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<AuthResponse> callback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam String state
    ) {
        if (!stateStore.validateAndRemove(state)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                oAuthService.handleCallback(provider, code)
        );
    }

}
