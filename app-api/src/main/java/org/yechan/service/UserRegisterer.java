package org.yechan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.UserRegisterUseCase;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterer implements UserRegisterUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterSuccessResponse registerUser(final UserRegisterRequest request) {
        String email = request.email();
        String name = request.name();
        String password = request.password();
        String phone = request.phone();
        // 이메일 중복 검증
        validateEmailUniqueness(email);

        // 이름 중복 검증
        validateUsernameUniqueness(name);

        // 전화번호 중복 검증
        validatePhoneNumberUniqueness(phone);

        // 저장
        registerVerifiedUser(name, email, password, phone);

        return new RegisterSuccessResponse(name, email);
    }

    @Transactional
    protected void registerVerifiedUser(final String name, final String email, final String password, final String phone) {
        String encryptedPassword = passwordEncoder.encode(password);
        userRepository.insertUser(name, email, encryptedPassword, phone);
    }

    @Transactional(readOnly = true)
    protected void validatePhoneNumberUniqueness(final String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new UserException("User with phone " + phone + " already exists.", UserErrorCode.PHONE_DUPLICATED);
        }
    }

    @Transactional(readOnly = true)
    protected void validateEmailUniqueness(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("User with email " + email + " already exists.", UserErrorCode.EMAIL_DUPLICATED);
        }
    }

    @Transactional(readOnly = true)
    protected void validateUsernameUniqueness(final String name) {
        if (userRepository.existsByName(name)) {
            throw new UserException("User with username " + name + " already exists.", UserErrorCode.NAME_DUPLICATED);
        }
    }
}
