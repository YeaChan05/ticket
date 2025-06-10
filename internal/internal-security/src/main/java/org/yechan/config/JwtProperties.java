package org.yechan.config;

public record JwtProperties(String secret, long accessExpiration) {
}
