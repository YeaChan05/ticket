package org.yechan.testdata;

import static java.util.Objects.requireNonNull;
import static org.yechan.service.TokenIssuerType.SELLER;
import static org.yechan.service.TokenIssuerType.USER;

import org.yechan.config.response.ApiResponse;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.entity.Seller;
import org.yechan.entity.User;
import org.yechan.fixture.TestFixture;

public class TestTokenGenerator {

    private static final String PATH = "/api/v1/auth/token";

    public static String generateToken(Class<?> userType, TestFixture fixture, IssueTokenRequest issueTokenRequest) {
        ApiResponse<TokenHolder> apiResponse;
        if (userType.equals(User.class) || userType.equals(Seller.class)) {
            var issuerType = userType.equals(User.class) ? USER : SELLER;
            apiResponse = fixture.post(
                            PATH,
                            issueTokenRequest,
                            null
                    )
                    .queryParam("issuerType", issuerType)
                    .exchange(TokenHolder.class)
                    .getApiResponse();
        } else {
            throw new IllegalArgumentException("지원하지 않는 사용자 유형입니다: " + userType.getSimpleName());
        }
        return requireNonNull(apiResponse).getData().accessToken();
    }

}
