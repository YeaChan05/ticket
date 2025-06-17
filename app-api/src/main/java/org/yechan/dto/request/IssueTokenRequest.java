package org.yechan.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.yechan.dto.annotation.Email;

public record IssueTokenRequest(
        @Email
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password) {
}
