package org.yechan.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum SellerErrorCode implements ErrorCode {
    DUPLICATED_EMAIL("SELLER-001","이메일이 이미 존재합니다" ),
    DUPLICATED_SELLER_NAME("SELLER-002","판매자 이름이 이미 존재합니다"),
    DUPLICATED_CONTACT("SELLER-003", "연락처가 이미 존재합니다");

    private final String code;
    private final String message;
}
