package org.yechan.fixture;

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
    private String token;

    RequestExecutor(TestFixture fixture, HttpMethod method, String url, Object body, Object... uriVariables) {
        this.fixture = fixture;
        this.method = method;
        this.url = url;
        this.body = body;
        this.uriVariables = uriVariables;
    }

    public RequestExecutor withToken(String token) {
        this.token = token;
        return this;
    }

    public RequestExecutor withoutToken() {
        this.token = null;
        return this;
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
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            return builder.body(body);
        }
        return builder.build();
    }
}
