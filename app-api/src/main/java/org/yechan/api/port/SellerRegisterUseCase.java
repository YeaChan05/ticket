package org.yechan.api.port;

import org.yechan.dto.response.SuccessfulSellerRegisterResponse;

public interface SellerRegisterUseCase {
    SuccessfulSellerRegisterResponse registerSeller(String sellerName, String email, String contact, String password);
}
