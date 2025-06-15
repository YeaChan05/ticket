package org.yechan;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.yechan.config.response.ApiResponse;
import org.yechan.dto.request.UserRegisterRequest;

public record TestFixture(
        TestRestTemplate client
) {
    public static String generateEmail() {
        String uuid = UUID.randomUUID().toString();
        String localPart = uuid.replace("-", "");
        return localPart + "@test.com";
    }

    public static String generateUsername() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }

    public static String generatePhone() {
        int middle = ThreadLocalRandom.current().nextInt(0, 10000);
        int last = ThreadLocalRandom.current().nextInt(0, 10000);
        return String.format("010-%04d-%04d", middle, last);
    }

    public static UserRegisterRequest generateUserRegisterRequest() {
        var username = generateUsername();

        var email = generateEmail();

        var phone = generatePhone();

        return new UserRegisterRequest(
                username,
                email,
                "Password123!",// 중복되어도 상관 없음
                phone
        );
    }

    public <T> ApiResponse<T> get(
            String url,
            Class<T> responseDataClass,
            Object... uriVariables
    ) {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(url, uriVariables)
                .build();

        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        ParameterizedTypeReference<ApiResponse<T>> responseType = ParameterizedTypeReference.forType(resolvableType.getType());

        ResponseEntity<ApiResponse<T>> response = client.exchange(requestEntity, responseType);
        return response.getBody();
    }

    public <T> ApiResponse<T> post(
            String url,
            Object requestBody,
            Class<T> responseDataClass,
            Object... uriVariables
    ) {
        RequestEntity<Object> requestEntity = RequestEntity
                .post(url, uriVariables)
                .body(requestBody);

        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        ParameterizedTypeReference<ApiResponse<T>> responseType = ParameterizedTypeReference.forType(resolvableType.getType());

        return client.exchange(requestEntity, responseType).getBody();
    }
}
