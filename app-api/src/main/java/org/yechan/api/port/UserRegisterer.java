package org.yechan.api.port;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRegisterer implements UserRegisterUseCase {
    private final UserRepository userRepository;

    @Override
    public RegisterSuccessResponse registerUser(final String name, final String email,
                                                final String password, final String phone) {
        // 이메일 중복 검증
        validateEmailUniqueness(email);

        // 이름 중복 검증
        validateUsernameUniqueness(name);

        // 저장
        registerVerifiedUser(name, email, password, phone);

        return new RegisterSuccessResponse(name, email);
    }

    @Transactional
    protected void registerVerifiedUser(final String name, final String email, final String password, final String phone) {
        userRepository.insertUser(name, email, password, phone);
    }

    protected void validateEmailUniqueness(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("User with email " + email + " already exists.", UserErrorCode.EMAIL_DUPLICATED);
        }
    }

    protected void validateUsernameUniqueness(final String name) {
        if (userRepository.existByName(name)) {
            throw new UserException("User with name " + name + " already exists.", UserErrorCode.NAME_DUPLICATED);
        }
    }
}
