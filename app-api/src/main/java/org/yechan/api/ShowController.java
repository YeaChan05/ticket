package org.yechan.api;

import static org.yechan.api.port.AuthUser.UserType.SELLER;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.api.port.AuthUser;
import org.yechan.api.port.ShowRegisterUseCase;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Seller;

@RequestMapping("/api/v1/shows")
@RestController
public record ShowController(
        ShowRegisterUseCase showRegisterUseCase
) {

    @PostMapping
    public ShowRegisterResponse registerShow(@Valid @RequestBody ShowRegisterRequest request,
                                             @AuthUser(type = SELLER) Seller seller) {
        return showRegisterUseCase.register(request, seller);
    }
}
