package org.yechan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yechan.api.port.UserRegisterUseCase;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.SuccessfulUserRegisterResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterer implements UserRegisterUseCase {
    private final UserPersister userPersister;
    private final UserValidator userValidator;


    @Override
    @Transactional
    public SuccessfulUserRegisterResponse registerUser(final UserRegisterRequest request) {
        // 이메일 중복 검증
        userValidator.validateEmailUniqueness(request.email());

        // 이름 중복 검증
        userValidator.validateUsernameUniqueness(request.username());

        // 전화번호 중복 검증
        userValidator.validatePhoneNumberUniqueness(request.phone());

        // 저장
        userPersister.registerVerifiedUser(request.username(), request.email(), request.password(), request.phone());

        return new SuccessfulUserRegisterResponse(request.username(), request.email());
    }
}
