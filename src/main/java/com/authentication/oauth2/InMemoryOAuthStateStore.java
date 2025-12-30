package com.authentication.oauth2;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryOAuthStateStore {

    private final Set<String> states = ConcurrentHashMap.newKeySet();

    public void store(String state) {
        states.add(state);
    }

    public boolean validateAndRemove(String state) {
        return states.remove(state);
    }
}
