package org.yechan.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.api.port.UserRegisterUseCase;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;

@RestController
@RequestMapping("/api/v1/users")
public record UserController(
        UserRegisterUseCase userRegisterUseCase
) {
    @PostMapping("/sign-up")
    public RegisterSuccessResponse registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return userRegisterUseCase.registerUser(userRegisterRequest);
    }
}
