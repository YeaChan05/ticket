package org.yechan.api.port;

import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Seller;

public interface ShowRegisterUseCase {
    ShowRegisterResponse register(ShowRegisterRequest request, Seller seller);
}
