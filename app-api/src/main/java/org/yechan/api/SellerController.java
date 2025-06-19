package org.yechan.api;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yechan.api.port.SellerRegisterUseCase;
import org.yechan.dto.request.SellerRegisterRequest;
import org.yechan.dto.response.SuccessfulSellerRegistrationResponse;

@RestController
@RequestMapping("/api/v1/sellers")
public record SellerController(SellerRegisterUseCase sellerRegisterUseCase) {
    @PostMapping("/sign-up")
    public SuccessfulSellerRegistrationResponse registerSeller(@Valid @RequestBody SellerRegisterRequest request) {
        return sellerRegisterUseCase.registerSeller(
                request.sellerName(),
                request.email(),
                request.contact(),
                request.password()
        );
    }
}
