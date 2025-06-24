package org.yechan.config.security;

public record JwtProperties(String secret, long accessExpiration) {
}
