package org.yechan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yechan.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
