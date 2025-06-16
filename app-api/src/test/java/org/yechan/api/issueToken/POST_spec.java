package org.yechan.api.issueToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.EmailGenerator.generateEmail;
import static org.yechan.PhoneNumberGenerator.generatePhone;
import static org.yechan.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.TestFixture;
import org.yechan.config.IntegrationTest;
import org.yechan.config.response.ResponseWrapper;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;

@IntegrationTest
@DisplayName("POST /api/v1/auth/issueToken")
public class POST_spec {
    @Autowired
    private ResponseWrapper responseWrapper;

    private static void generateUser(final TestFixture fixture, final String name, final String email,
                                     final String password, final String phone) {
        fixture.post(
                "/api/v1/users/sign-up",
                new UserRegisterRequest(name, email, password, phone),
                RegisterSuccessResponse.class
        );
    }

    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_200_응답이_반환된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var name = generateUsername();
        var email = generateEmail();
        var password = "securep!21Assword";
        var phone = generatePhone();

        generateUser(fixture, name, email, password, phone);

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/issueToken",
                        request,
                        TokenHolder.class
                )
                .onSuccess(
                        response -> {
                            // Assert
                            var body = response.getData();
                            assertThat(body).isNotNull();
                            assertThat(body.accessToken()).isNotBlank();
                        }
                );
    }

    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_JWT_토큰이_반환된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();
        var password = "securep!21Assword";
        var name = generateUsername();
        var phone = generatePhone();
        generateUser(fixture, name, email, password, phone);

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/issueToken",
                        request,
                        TokenHolder.class
                )
                .onSuccess(
                        response -> {
                            // Assert
                            var body = response.getData();
                            assertThat(body).isNotNull();
                            assertThat(body.accessToken()).isNotBlank();
                        }
                );
    }

    @Test
    @DisplayName("로그인 시도 시 등록되지 않은 email이면 USER-001 예외가 발생한다")
    void 로그인_시도_시_등록되지_않은_email이면_USER_001_예외가_발생한다(
            @Autowired TestFixture fixture
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
        fixture.post(
                        "/api/v1/auth/issueToken",
                        request,
                        TokenHolder.class
                )
                .onError(response -> {
                    // Assert
                    assertThat(response).isNotNull();
                    assertThat(response.getStatus()).isEqualTo("USER-001");
                    assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
                });
    }

    @Test
    void 로그인_시도_시_email에_상응하는_비밀번호가_누락이면_CONSTRAINT_VIOLATION_예외가_발생한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        String email = generateEmail();

        // Act
        fixture.post(
                        "/api/v1/auth/issueToken",
                        new IssueTokenRequest(email, null),// 비밀번호가 누락된 경우
                        TokenHolder.class
                )
                .onError(
                        response -> {
                            // Assert
                            assertThat(response).isNotNull();
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                            assertThat(response.getMessage()).isEqualTo("비밀번호를 입력해주세요.");
                        }
                );
    }

    @Test
    @DisplayName("로그인 시도 시 잘못된 비밀번호를 입력하면 USER-003 예외가 발생한다")
    void 로그인_시도_시_잘못된_비밀번호를_입력하면_USER_003_예외가_발생한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var password = "securep!21Assword";
        var email = generateEmail();
        generateUser(fixture, generateUsername(), email, password, generatePhone());
        var wrongPassword = "wrongPassword!23";
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                wrongPassword
        );

        // Act
        fixture.post(
                        "/api/v1/auth/issueToken",
                        request,
                        TokenHolder.class)
                .onError(
                        response -> {
                            // Assert
                            assertThat(response).isNotNull();
                            assertThat(response.getStatus()).isEqualTo("USER-003");
                            assertThat(response.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
                        }
                );
    }
}
