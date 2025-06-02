package org.yechan.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    EMAIL_DUPLICATED("EMAIL-001", "이미 사용중인 이메일입니다."),
    NAME_DUPLICATED("EMAIL-002", "중복된 이름입니다."),
    PHONE_DUPLICATED("EMAIL-003", "이미 사용중인 연락처입니다.");

    private final String code;
    private final String message;
}
