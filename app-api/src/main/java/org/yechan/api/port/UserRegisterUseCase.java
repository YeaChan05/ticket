package org.yechan.api.port;

import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;

public interface UserRegisterUseCase {
    RegisterSuccessResponse registerUser(UserRegisterRequest userRegisterRequest);
}
