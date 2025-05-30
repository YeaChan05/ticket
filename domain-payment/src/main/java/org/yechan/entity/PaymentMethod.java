package org.yechan.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentMethod {
    CREDIT_CARD("신용카드"),
    NAVER_PAY("네이버 페이"),
    TOSS("토스"),
    BANK_TRANSFER("은행 송금"),;

    private final String description;
}
