package org.yechan.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum SellerErrorCode implements ErrorCode {
    DUPLICATED_EMAIL("SELLER-001","이메일이 이미 존재합니다" ),
    DUPLICATED_SELLER_NAME("SELLER-002","판매자 이름이 이미 존재합니다"),
    DUPLICATED_CONTACT("SELLER-003", "연락처가 이미 존재합니다"),
    SELLER_NOT_FOUND("SELLER-004", "판매자를 찾을 수 없습니다"),
    PASSWORD_MISMATCH("SELLER-005","비밀번호가 일치하지 않습니다." );

    private final String code;
    private final String message;
}
