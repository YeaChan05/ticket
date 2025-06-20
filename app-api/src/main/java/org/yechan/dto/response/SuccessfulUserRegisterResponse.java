package org.yechan.dto.response;

public record SuccessfulUserRegisterResponse(
        String username,
        String email
) {
}
