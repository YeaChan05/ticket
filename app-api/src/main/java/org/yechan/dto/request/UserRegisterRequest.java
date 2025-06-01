package org.yechan.dto.request;

import org.yechan.config.annotation.Email;

public record UserRegisterRequest(
        String name,
        @Email String email,
        String password,
        String phone
) {
}
