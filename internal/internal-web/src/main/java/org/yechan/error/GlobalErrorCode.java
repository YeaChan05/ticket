package org.yechan.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    INVALID_REQUEST("CONSTRAINT_VIOLATION", "잘못된 요청입니다."),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    INVALID_SIGNATURE("INVALID_SIGNATURE", "유효하지 않은 서명입니다."),
    MALFORMED_TOKEN("MALFORMED_TOKEN", "잘못된 토큰 형식입니다."),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 토큰입니다."),
    EMPTY_TOKEN("EMPTY_TOKEN", "토큰이 비어 있습니다."),
    UNSUPPORTED_SIGNATURE_ALGORITHM("UNSUPPORTED_SIGNATURE_ALGORITHM","지원되지 않는 서명 알고리즘입니다." );

    private final String code;
    private final String message;
}
