package org.yechan.fixture;

import static java.util.Objects.requireNonNull;
import static org.yechan.service.TokenIssuerType.SELLER;
import static org.yechan.service.TokenIssuerType.USER;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PasswordGenerator.generatePassword;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.request.SellerRegisterRequest;
import org.yechan.dto.response.SuccessfulSellerRegisterResponse;
import org.yechan.entity.Seller;
import org.yechan.entity.User;

public record TestFixture(
        TestRestTemplate client,
        ObjectMapper objectMapper
) {
    private static final String PATH = "/api/v1/auth/token";

    private static final Logger log = LoggerFactory.getLogger(TestFixture.class);

    public RequestExecutor get(
            String url,
            @Nullable String token
    ) {
        return new RequestExecutor(this, HttpMethod.GET, url, null, token);
    }

    public RequestExecutor post(
            String url,
            Object requestBody,
            @Nullable String token
    ) {
        return new RequestExecutor(this, HttpMethod.POST, url, requestBody, token);
    }

    public String generateToken(Class<?> userType) {
        ApiResponse<TokenHolder> apiResponse;
        if (userType.equals(User.class) || userType.equals(Seller.class)) {
            var issuerType = userType.equals(User.class) ? USER : SELLER;
            apiResponse = post(
                    PATH,
                    generateSeller(),
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

    <T> TestResult<T> parseResponseBody(
            final String responseBody, final ParameterizedTypeReference<ApiResponse<T>> responseType) {
        try {
            JavaType javaType = objectMapper.constructType(responseType.getType());
            ApiResponse<T> successResponse = objectMapper.readValue(responseBody, javaType);
            return TestResult.success(successResponse);
        } catch (JsonProcessingException e) {
            return TestResult.error(parseToErrorResponse(responseBody));
        }
    }

    ErrorResponse parseToErrorResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (JsonProcessingException ex) {
            log.error("response {}", responseBody);
            throw new RuntimeException(
                    "Failed to parse response body into any known type (ApiResponse or ErrorResponse)", ex);
        }
    }

    <T> ParameterizedTypeReference<ApiResponse<T>> getResponseType(
            final Class<T> responseDataClass) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }

    public IssueTokenRequest generateSeller() {
        var email = generateEmail();
        var password = generatePassword();
        post(
                "/api/v1/sellers/sign-up",
                new SellerRegisterRequest(
                        generateUsername(),
                        email,
                        password,
                        generatePhone()
                ),
                null
        )
                .exchange(SuccessfulSellerRegisterResponse.class)
                .getApiResponse();

        return new IssueTokenRequest(
                email,
                password
        );
    }
}
