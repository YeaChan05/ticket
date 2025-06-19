package org.yechan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.error.SellerErrorCode;
import org.yechan.error.exception.SellerException;
import org.yechan.repository.SellerRepository;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerValidator {
    private final SellerRepository sellerRepository;

    public void validateSellerNameUniqueness(String sellerName) {
        if (sellerRepository.existsBySellerName(sellerName)) {
            throw new SellerException("판매자 이름이 이미 존재합니다", SellerErrorCode.DUPLICATED_SELLER_NAME);
        }
    }

    public void validateEmailUniqueness(String email) {
        if (sellerRepository.existsByEmail(email)) {
            throw new SellerException("이메일이 이미 존재합니다", SellerErrorCode.DUPLICATED_EMAIL);
        }
    }

    public void validateContactUniqueness(String contact) {
        if (sellerRepository.existsByContact(contact)) {
            throw new SellerException("연락처가 이미 존재합니다", SellerErrorCode.DUPLICATED_CONTACT);
        }
    }
}
