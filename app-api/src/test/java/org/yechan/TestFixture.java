package org.yechan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;

public record TestFixture(
        TestRestTemplate client,
        ObjectMapper objectMapper
) {

    public <T> TestResult<ApiResponse<T>, ErrorResponse> get(
            String url,
            Class<T> responseDataClass,
            Object... uriVariables
    ) {
        RequestEntity<Void> requestEntity = RequestEntity.get(url, uriVariables).build();
        return processRequest(requestEntity, getResponseType(responseDataClass));
    }

    public <T> TestResult<ApiResponse<T>, ErrorResponse> post(
            String url,
            Object requestBody,
            Class<T> responseDataClass,
            Object... uriVariables
    ) {
        RequestEntity<Object> requestEntity = RequestEntity.post(url, uriVariables).body(requestBody);
        return processRequest(requestEntity, getResponseType(responseDataClass));
    }

    private <T> TestResult<ApiResponse<T>, ErrorResponse> processRequest(
            final RequestEntity<?> requestEntity, final ParameterizedTypeReference<ApiResponse<T>> responseType) {
        try {
            ResponseEntity<String> responseEntity = client.exchange(requestEntity, String.class);
            return parseResponseBody(responseEntity.getBody(), responseType);
        } catch (RestClientResponseException e) {
            return TestResult.error(parseToErrorResponse(e.getResponseBodyAsString()));
        }
    }



    private <T> TestResult<ApiResponse<T>, ErrorResponse> parseResponseBody(
            final String responseBody, final ParameterizedTypeReference<ApiResponse<T>> responseType) {
        try {
            JavaType javaType = objectMapper.constructType(responseType.getType());
            ApiResponse<T> successResponse = objectMapper.readValue(responseBody, javaType);
            return TestResult.success(successResponse);
        } catch (JsonProcessingException e) {
            return TestResult.error(parseToErrorResponse(responseBody));
        }
    }

    private ErrorResponse parseToErrorResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to parse response body into any known type (ApiResponse or ErrorResponse)", ex);
        }
    }

    private <T> ParameterizedTypeReference<ApiResponse<T>> getResponseType(
            final Class<T> responseDataClass) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }
}
