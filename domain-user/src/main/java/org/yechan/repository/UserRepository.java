package org.yechan.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yechan.entity.User;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public boolean existsByEmail(final String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    public boolean existsByName(final String name) {
        return jpaUserRepository.existsByName(name);
    }

    public boolean existsByPhone(final String phone) {
        return jpaUserRepository.existsByPhone(phone);
    }

    public void insertUser(final String name, final String email, final String encryptedPassword, final String phone) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(encryptedPassword)
                .phone(phone)
                .build();
        jpaUserRepository.save(user);
    }

    public Optional<User> findByEmail(final String email) {
        return jpaUserRepository.findByEmail(email);
    }
}
