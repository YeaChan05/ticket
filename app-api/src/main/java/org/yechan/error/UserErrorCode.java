package org.yechan.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    EMAIL_DUPLICATED("EMAIL-001", "이미 사용중인 이메일입니다."),
    NAME_DUPLICATED("EMAIL-001", "중복된 이름입니다.");

    private final String code;
    private final String message;
}
