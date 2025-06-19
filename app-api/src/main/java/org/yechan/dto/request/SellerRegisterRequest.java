package org.yechan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.yechan.dto.annotation.Email;

public record SellerRegisterRequest(
        @NotBlank(message = "판매자 이름은 비워둘 수 없습니다.")
        String sellerName,

        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        @Email
        String email,

        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotBlank(message = "전화번호는 비워둘 수 없습니다.")
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-xxxx-xxxx이어야 합니다.")
        String contact

) {
}
