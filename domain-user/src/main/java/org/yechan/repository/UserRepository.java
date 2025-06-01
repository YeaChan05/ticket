package org.yechan.repository;

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

    public boolean existByName(final String name) {
        return jpaUserRepository.existsByName(name);
    }

    public void insertUser(final String name, final String email, final String password, final String phone) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phone(phone)
                .build();
        jpaUserRepository.save(user);
    }
}
