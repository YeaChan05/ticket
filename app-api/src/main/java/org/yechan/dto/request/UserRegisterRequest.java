package org.yechan.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.yechan.config.annotation.Email;

public record UserRegisterRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "이메일을 입력해주세요.")
        @Email
        String email,

        @NotNull(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotNull(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-xxxx-xxxx이어야 합니다.")
        String phone
) {
}
