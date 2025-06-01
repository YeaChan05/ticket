package org.yechan.api.port;

import org.yechan.dto.response.RegisterSuccessResponse;

public interface UserRegisterUseCase {
    RegisterSuccessResponse registerUser(String name, String email, String password, String phone);
}
