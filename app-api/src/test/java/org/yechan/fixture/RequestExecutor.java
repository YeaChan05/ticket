package org.yechan.fixture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.yechan.config.response.ApiResponse;

public class RequestExecutor {

    private final TestFixture fixture;
    private final HttpMethod method;
    private final String url;
    private final Object body;
    private final Object[] uriVariables;
    private final String token;

    RequestExecutor(TestFixture fixture, HttpMethod method, String url, Object body, String token,
                    Object... uriVariables) {
        this.fixture = fixture;
        this.method = method;
        this.url = url;
        this.body = body;
        this.token = token;
        this.uriVariables = uriVariables;
    }

    public <T> TestResult<T> exchange(Class<T> responseDataClass) {
        RequestEntity<?> requestEntity = buildRequestEntity();
        try {
            ParameterizedTypeReference<ApiResponse<T>> responseType = fixture.getResponseType(responseDataClass);
            ResponseEntity<String> responseEntity = fixture.client().exchange(requestEntity, String.class);
            return fixture.parseResponseBody(responseEntity.getBody(), responseType);
        } catch (RestClientResponseException e) {
            return TestResult.error(fixture.parseToErrorResponse(e.getResponseBodyAsString()));
        }
    }

    private RequestEntity<?> buildRequestEntity() {
        BodyBuilder builder = RequestEntity.method(method, url, uriVariables);
        builder.header("Content-Type", APPLICATION_JSON_VALUE);
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            return builder.body(body);
        }
        return builder.build();
    }
}
