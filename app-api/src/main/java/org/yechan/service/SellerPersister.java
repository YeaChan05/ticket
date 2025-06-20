package org.yechan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.entity.Seller;
import org.yechan.repository.SellerRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class SellerPersister {
    private final SellerRepository repository;
    private final PasswordEncoder encoder;

    public void registerVerifiedSeller(String sellerName, String email, String contact, String password) {
        Seller seller = Seller.builder()
                .password(encoder.encode(password))
                .email(email)
                .contact(contact)
                .name(sellerName)
                .build();

        repository.insertSeller(seller);
    }
}
