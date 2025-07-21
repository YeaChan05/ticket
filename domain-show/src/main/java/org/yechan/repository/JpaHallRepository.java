package org.yechan.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yechan.entity.Hall;

public interface JpaHallRepository extends JpaRepository<Hall, Long> {
    Optional<Hall> findByHallKey(UUID hallKey);
}
