package org.yechan.config.security;

import lombok.Getter;

@Getter
public enum ClaimKey {
    ROLE("role"),
    EMAIL("email"),
    USERNAME("username");

    private final String key;

    ClaimKey(String key) {
        this.key = key;
    }
}
