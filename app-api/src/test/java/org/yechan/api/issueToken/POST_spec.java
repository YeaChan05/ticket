package org.yechan.api.issueToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.api.fixture.UserFixture.generateEmail;
import static org.yechan.api.fixture.UserFixture.generatePhone;
import static org.yechan.api.fixture.UserFixture.generateUsername;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.yechan.api.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.request.UserRegisterRequest;

@IntegrationTest
@DisplayName("POST /api/v1/auth/issueToken")
public class POST_spec {
    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_200_응답이_반환된다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var name = generateUsername();
        var email = generateEmail();
        var password = "securep!21Assword";
        var phone = generatePhone();

        client.postForObject(
                URI.create("/api/v1/users/sign-up"),
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

    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_JWT_토큰이_반환된다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var email = generateEmail();
        var password = "securep!21Assword";
        var name = generateUsername();
        var phone = generatePhone();
        client.postForObject(
                URI.create("/api/v1/users/sign-up"),
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
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void 로그인_시도_시_등록되지_않은_email이면_USER_예외가_발생한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        var email = generateEmail();
        var password = "securep!21Assword";

        // not registering the user
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        var response = client.postForObject(
                URI.create("/api/v1/auth/issueToken"),
                request,
                ErrorResponse.class
        );

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("USER-001");
        assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    void 로그인_시도_시_email에_상응하는_비밀번호가_누락이면_USER_예외가_발생한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = null;// 비밀번호가 누락된 경우

        // Act
        var response = client.postForObject(
                "/ai/v1/auth/issueToken",
                new IssueTokenRequest(email, password),
                ErrorResponse.class
        );

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("USER-003");
    }
}
