package org.yechan.dto.request;

import jakarta.validation.constraints.NotNull;
import org.yechan.dto.annotation.Email;

public record IssueTokenRequest(
        @Email
        @NotNull(message = "이메일을 입력해주세요.")
        String email,

        @NotNull(message = "비밀번호를 입력해주세요.")
        String password) {
}
