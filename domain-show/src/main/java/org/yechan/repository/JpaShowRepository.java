package org.yechan.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yechan.entity.Show;

public interface JpaShowRepository extends JpaRepository<Show, Long> {
    boolean existsByTitle(String title);
    boolean existsByKey(UUID key);
}
