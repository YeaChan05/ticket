package org.yechan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yechan.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(1) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(1) > 0 FROM User u WHERE u.name = :name")
    boolean existsByName(@Param("name") String name);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);
}
