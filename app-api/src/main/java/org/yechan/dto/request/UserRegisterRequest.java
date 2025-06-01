package org.yechan.dto.request;

import jakarta.validation.constraints.NotNull;
import org.yechan.config.annotation.Email;

public record UserRegisterRequest(
        @NotNull(message = "이름을 입력해주세요.") String name,
        @NotNull(message = "이메일을 입력해주세요.") @Email String email,
        @NotNull(message = "비밀번호를 입력해주세요.") String password,
        @NotNull(message = "전화번호를 입력해주세요.") String phone
) {
}
