package org.yechan.dto.response;

import org.yechan.entity.User.Role;

public record RegisterSuccessResponse(
        String name,
        String email,
        Role role
) {
}
