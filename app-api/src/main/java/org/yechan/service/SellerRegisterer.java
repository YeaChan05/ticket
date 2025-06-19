package org.yechan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.SellerRegisterUseCase;
import org.yechan.dto.response.SuccessfulSellerRegisterResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerRegisterer implements SellerRegisterUseCase {
    private final SellerPersister sellerPersister;
    private final SellerValidator sellerValidator;
    @Override
    public SuccessfulSellerRegisterResponse registerSeller(String sellerName,
                                                           String email,
                                                           String contact,
                                                           String password) {
        // 이메일 중복 검증
        sellerValidator.validateEmailUniqueness(email);

        // 판매자 이름 중복 검증
        sellerValidator.validateSellerNameUniqueness(sellerName);

        // 판매자 전화번호 중복 검증
        sellerValidator.validateContactUniqueness(contact);

        // 판매자 정보 저장
        sellerPersister.registerVerifiedSeller(sellerName, email, contact, password);

        return new SuccessfulSellerRegisterResponse(sellerName, email);
    }
}
