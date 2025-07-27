package org.yechan.fixture;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.yechan.config.response.ApiResponse;

public class RequestExecutor {

    private final TestFixture fixture;
    private final HttpMethod method;
    private final String url;
    private final Object body;
    private final String token;
    private Map<String, Object> queryParams;

    RequestExecutor(TestFixture fixture, HttpMethod method, String url, Object body, String token) {
        this.fixture = fixture;
        this.method = method;
        this.url = url;
        this.body = body;
        this.token = token;
    }

    public RequestExecutor queryParam(String key, Object value) {
        if (queryParams == null) {
            this.queryParams = new HashMap<>();
        }
         queryParams.put(key, value);
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
        String finalUrl = url;
        BodyBuilder builder;
        if (queryParams != null && !queryParams.isEmpty()) {
            UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(url);
            queryParams.forEach(ucb::queryParam);
            finalUrl = ucb.build().toUriString();
        }
        builder = RequestEntity.method(method, finalUrl);
        builder.headers(h -> h.setContentType(APPLICATION_JSON));
        if (token != null && !token.isBlank()) {
            builder.headers(h-> h.setBearerAuth(token));
        }
        if (body != null) {
            return builder.body(body);
        }
        return builder.build();
    }
}
