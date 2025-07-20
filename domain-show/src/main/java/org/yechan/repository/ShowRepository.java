package org.yechan.repository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.yechan.entity.Show;

@Repository
@RequiredArgsConstructor
public class ShowRepository {
    private final JpaShowRepository jpaShowRepository;

    public UUID insert(Show show) throws DataIntegrityViolationException {
        Show savedShow = jpaShowRepository.save(show);
        return savedShow.getKey();
    }

    public boolean existByTitle(String title) {
        return jpaShowRepository.existsByTitle(title);
    }

    public boolean existByKey(UUID key) {
        return jpaShowRepository.existsByKey(key);
    }
}
