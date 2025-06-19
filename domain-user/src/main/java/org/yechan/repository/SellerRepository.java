package org.yechan.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yechan.entity.Seller;

@Repository
@RequiredArgsConstructor
public class SellerRepository {
    private final JpaSellerRepository jpaSellerRepository;

    public boolean existsByEmail(String email) {
        return jpaSellerRepository.existByEmail(email);
    }

    public boolean existsByContact(String contact) {
        return jpaSellerRepository.existByContact(contact);
    }

    public boolean existsBySellerName(String sellerName) {
        return jpaSellerRepository.existBySellerName(sellerName);
    }

    public void insertSeller(Seller seller) {
        jpaSellerRepository.save(seller);
    }
}
