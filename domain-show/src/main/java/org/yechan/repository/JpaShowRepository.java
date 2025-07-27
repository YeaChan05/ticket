package org.yechan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yechan.entity.Show;

public interface JpaShowRepository extends JpaRepository<Show, Long> {
    boolean existsByTitle(String title);
}
