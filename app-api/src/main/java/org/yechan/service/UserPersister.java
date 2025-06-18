package org.yechan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserPersister {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void registerVerifiedUser(final String name,
                                     final String email,
                                     final String password,
                                     final String phone) {
        String encryptedPassword = passwordEncoder.encode(password);
        userRepository.insertUser(name, email, encryptedPassword, phone);
    }
}
