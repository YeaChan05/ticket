package org.yechan.api.port;

import org.yechan.dto.response.SuccessfulSellerRegistrationResponse;

public interface SellerRegisterUseCase {
    SuccessfulSellerRegistrationResponse registerSeller(String sellerName, String email, String contact, String password);
}
