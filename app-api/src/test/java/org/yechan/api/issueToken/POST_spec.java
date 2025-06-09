package org.yechan.api.issueToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.api.fixture.UserFixture.generateUserRegisterRequest;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.yechan.api.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.dto.request.IssueTokenRequest;

@IntegrationTest
public class POST_spec {
    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_200_응답이_반환된다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var userRegisterRequest = generateUserRegisterRequest();
        var email = userRegisterRequest.email();
        var password = userRegisterRequest.password();
        client.postForObject(
                "/api/v1/users/sign-up",
                userRegisterRequest,
                ApiResponse.class
        );

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        var response = client.postForObject(
                URI.create("/api/v1/auth/issueToken"),
                request,
                ApiResponse.class
        );

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_JWT_토큰이_반환된다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var userRegisterRequest = generateUserRegisterRequest();
        var email = userRegisterRequest.email();
        var password = userRegisterRequest.password();
        client.postForObject(
                "/api/v1/users/sign-up",
                userRegisterRequest,
                ApiResponse.class
        );

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        var response = client.postForObject(
                URI.create("/api/v1/auth/issueToken"),
                request,
                ApiResponse.class
        );

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotNull();
    }
}
