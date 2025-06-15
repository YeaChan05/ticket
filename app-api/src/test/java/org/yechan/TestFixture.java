package org.yechan;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.yechan.config.response.ApiResponse;

public record TestFixture(
        TestRestTemplate client
) {

    public <T> ApiResponse<T> get(
            String url,
            Class<T> responseDataClass,
            Object... uriVariables
    ) {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(url, uriVariables)
                .build();

        var responseType = getResponseType(responseDataClass);

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

        var responseType = getResponseType(responseDataClass);

        return client.exchange(requestEntity, responseType).getBody();
    }

    private <T> ParameterizedTypeReference<ApiResponse<T>> getResponseType(
            final Class<T> responseDataClass) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }
}
