package org.yechan.api.port;

import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.SuccessfulUserRegisterResponse;

public interface UserRegisterUseCase {
    SuccessfulUserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest);
}
