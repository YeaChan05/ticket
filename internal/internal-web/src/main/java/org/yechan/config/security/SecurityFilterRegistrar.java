package org.yechan.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface SecurityFilterRegistrar {
    void register(HttpSecurity http);
}
