package org.yechan.repository;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yechan.entity.Hall;

@Repository
@RequiredArgsConstructor
public class HallRepository {
    private final JpaHallRepository jpaHallRepository;

    public Optional<Hall> getHallByKey(UUID key) {
        return jpaHallRepository.findByHallKey(key);
    }
}
