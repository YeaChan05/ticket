package org.yechan.repository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yechan.entity.Show;

@Repository
@RequiredArgsConstructor
public class ShowRepository{
    private final JpaShowRepository jpaShowRepository;

    public UUID insert(Show show) {
        var savedShow = jpaShowRepository.save(show);
        return savedShow.getKey();
    }
}
