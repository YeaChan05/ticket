package org.yechan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validatePhoneNumberUniqueness(final String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new UserException("User with phone " + phone + " already exists.", UserErrorCode.PHONE_DUPLICATED);
        }
    }

    public void validateEmailUniqueness(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("User with email " + email + " already exists.", UserErrorCode.EMAIL_DUPLICATED);
        }
    }

    public void validateUsernameUniqueness(final String username) {
        if (userRepository.existsByName(username)) {
            throw new UserException("User with username " + username + " already exists.", UserErrorCode.NAME_DUPLICATED);
        }
    }
}
