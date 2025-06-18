package org.yechan.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER-001", "사용자를 찾을 수 없습니다."),
    NAME_DUPLICATED("USER-002", "중복된 이름입니다."),
    PASSWORD_MISMATCH("USER-003", "비밀번호가 일치하지 않습니다."),
    PASSWORD_EMPTY("USER-004","비밀번호가 비어있습니다."),
    PHONE_DUPLICATED("USER-005", "이미 사용중인 연락처입니다."),
    EMAIL_DUPLICATED("USER-006", "이미 사용중인 이메일입니다.");

    private final String code;
    private final String message;
}
