package org.yechan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yechan.entity.Seller;

public interface JpaSellerRepository extends JpaRepository<Seller, Long> {
    @Query("SELECT COUNT(1) > 0 FROM Seller s WHERE s.email = :email")
    boolean existByEmail(@Param(value = "email") String email);

    @Query("SELECT COUNT(1) > 0 FROM Seller s WHERE s.contact = :contact")
    boolean existByContact(@Param(value = "contact") String contact);

    @Query("SELECT COUNT(1) > 0 FROM Seller s WHERE s.name = :sellerName")
    boolean existBySellerName(@Param(value = "sellerName") String sellerName);
}
