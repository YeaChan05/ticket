package org.yechan.api.issueToken;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.yechan.api.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.request.UserRegisterRequest;

@IntegrationTest
public class POST_spec {
    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_200_응답이_반환된다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = "user@test.com";
        String password = "securep!21Assword";

        String name="testUser";
        String phone = "010-1234-5678";

        client.postForObject(
                "/api/v1/users/sign-up",
                new UserRegisterRequest(name, email, password, phone),
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
}
