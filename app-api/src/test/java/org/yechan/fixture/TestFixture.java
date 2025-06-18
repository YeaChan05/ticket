package org.yechan.fixture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;

public record TestFixture(
        TestRestTemplate client,
        ObjectMapper objectMapper
) {

    public RequestExecutor get(
            String url,
            @Nullable String token,
            Object... uriVariables
    ) {
        return new RequestExecutor(this, HttpMethod.GET, url, null,token, uriVariables);
    }

    public RequestExecutor post(
            String url,
            Object requestBody,
            @Nullable String token,
            Object... uriVariables
    ) {
        return new RequestExecutor(this, HttpMethod.POST, url, requestBody,token, uriVariables);
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
            throw new RuntimeException("Failed to parse response body into any known type (ApiResponse or ErrorResponse)", ex);
        }
    }

    <T> ParameterizedTypeReference<ApiResponse<T>> getResponseType(
            final Class<T> responseDataClass) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ApiResponse.class, responseDataClass);
        return ParameterizedTypeReference.forType(resolvableType.getType());
    }
}
