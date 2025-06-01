package org.yechan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yechan.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT 1 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT 1 FROM User u WHERE u.name = :name")
    boolean existsByName(String name);
}
