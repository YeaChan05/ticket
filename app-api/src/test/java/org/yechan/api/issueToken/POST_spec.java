package org.yechan.api.issueToken;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.config.IntegrationTest;
import org.yechan.dto.TokenHolder;
import org.yechan.dto.request.IssueTokenRequest;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.fixture.TestFixture;

@IntegrationTest
@DisplayName("POST /api/v1/auth/token")
public class POST_spec {
    private static final String PASSWORD = "securep!21Assword";

    private static IssueTokenRequest[] invalidEmailProvider() {
        return new IssueTokenRequest[]{
                new IssueTokenRequest("invalid-email", "password123!"),
                new IssueTokenRequest("user@.com", "password123!"),
                new IssueTokenRequest("user@domain", "password123!"),
                new IssueTokenRequest("user@domain..com", "password123!"),
                new IssueTokenRequest("", "password123!"),
                new IssueTokenRequest(null, "password123!"),
                new IssueTokenRequest("valid@test.com", ""),
                new IssueTokenRequest("valid@test.com", null)
        };
    }

    private static String decodeBase64Url(String base64UrlString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(base64UrlString);
        return new String(decodedBytes, UTF_8);
    }

    @Test
    void 로그인_시도_시_등록된_email과_password로_요청하면_200_응답이_반환된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var name = generateUsername();
        var email = generateEmail();
        var password = PASSWORD;
        var phone = generatePhone();

        generateUser(fixture, name, email, password, phone);

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
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
        var password = PASSWORD;
        var name = generateUsername();
        var phone = generatePhone();
        generateUser(fixture, name, email, password, phone);

        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
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
    void 로그인_시도_시_email에_상응하는_비밀번호가_누락이면_CONSTRAINT_VIOLATION_예외가_발생한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        String email = generateEmail();

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        new IssueTokenRequest(email, null)// 비밀번호가 누락된 경우
                )
                .withoutToken()
                .exchange(TokenHolder.class)
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
    @DisplayName("로그인 시도 시 등록되지 않은 email이면 USER-001 예외가 발생한다")
    void 로그인_시도_시_등록되지_않은_email이면_USER_001_예외가_발생한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();

        // not registering the user
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                PASSWORD
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .onError(response -> {
                    // Assert
                    assertThat(response).isNotNull();
                    assertThat(response.getStatus()).isEqualTo("USER-001");
                    assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
                });
    }

    @Test
    @DisplayName("로그인 시도 시 잘못된 비밀번호를 입력하면 USER-003 예외가 발생한다")
    void 로그인_시도_시_잘못된_비밀번호를_입력하면_USER_003_예외가_발생한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var password = PASSWORD;
        var email = generateEmail();
        generateUser(fixture, generateUsername(), email, password, generatePhone());
        var wrongPassword = "wrongPassword!23";
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                wrongPassword
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request)
                .withoutToken()
                .exchange(TokenHolder.class)
                .onError(
                        response -> {
                            // Assert
                            assertThat(response).isNotNull();
                            assertThat(response.getStatus()).isEqualTo("USER-003");
                            assertThat(response.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
                        }
                );
    }

    @ParameterizedTest
    @MethodSource("org.yechan.api.issueToken.POST_spec#invalidEmailProvider")
    @DisplayName("로그인 파라미터가 잘못된 형식이면 CONSTRAINT_VIOLATION 예외가 발생한다")
    void 로그인_파라미터가_잘못된_형식이면_CONSTRAINT_VIOLATION_예외가_발생한다(
            IssueTokenRequest request,
            @Autowired TestFixture fixture
    ) {
        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .onError(
                        response -> {
                            // Assert
                            assertThat(response).isNotNull();
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                        }
                );

    }

    @Test
    void 로그인_성공_시_토큰은_JWT_형식을_따른다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();
        var password = PASSWORD;
        var username = generateUsername();
        var phone = generatePhone();
        generateUser(fixture, username, email, password, phone);
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .onSuccess(
                        response -> {
                            // Assert
                            var body = response.getData();
                            assertThat(body).isNotNull();
                            assertThat(body.accessToken()).isNotBlank();
                            assertThat(body.accessToken()).matches(
                                    "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$");
                            var parts = body.accessToken().split("\\.");
                            assertThat(parts).hasSize(3);

                            var header = decodeBase64Url(parts[0]);
                            assertThat(header).isNotBlank();
                            assertThat(header).contains("\"alg\":\"HS256\"");

                            var payload = decodeBase64Url(parts[1]);

                            assertThat(payload).contains("\"role\":\"USER\"");
                            assertThat(payload).contains("\"email\":\"" + email + "\"");
                            assertThat(payload).contains("\"username\":\"" + username + "\"");

                            var signature = decodeBase64Url(parts[2]);
                            assertThat(signature).isNotBlank();
                        }
                );

    }

    @Test
    void 토큰의_payload에_email_정보와_username이_포함된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();
        var password = PASSWORD;
        var username = generateUsername();
        var phone = generatePhone();
        generateUser(fixture, username, email, password, phone);
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .onSuccess(
                        response -> {
                            // Assert
                            var body = response.getData();
                            assertThat(body).isNotNull();
                            assertThat(body.accessToken()).isNotBlank();
                            var parts = body.accessToken().split("\\.");
                            var payload = decodeBase64Url(parts[1]);
                            assertThat(payload).contains("\"email\":\"" + email + "\"");
                            assertThat(payload).contains("\"username\":\"" + username + "\"");
                        }
                );

    }

    @Test
    void 여러_번_로그인_시도해도_항상_새로운_토큰이_발급된다(
            @Autowired TestFixture fixture
    ) throws InterruptedException {
        // Arrange
        var email = generateEmail();
        var password = PASSWORD;
        var username = generateUsername();
        var phone = generatePhone();
        generateUser(fixture, username, email, password, phone);
        IssueTokenRequest request = new IssueTokenRequest(
                email,
                password
        );

        // Act
        var apiResponse1 = fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .getApiResponse();
        var apiResponse2 = fixture.post(
                        "/api/v1/auth/token",
                        request
                )
                .withoutToken()
                .exchange(TokenHolder.class)
                .getApiResponse();

        // Assert
        var token1 = apiResponse1.getData().accessToken();
        var token2 = apiResponse2.getData().accessToken();
        assertThat(token1).isNotEqualTo(token2);
    }

    public void generateUser(final TestFixture fixture, final String name, final String email,
                             final String password, final String phone) {
        fixture.post(
                        "/api/v1/users/sign-up",
                        new UserRegisterRequest(name, email, password, phone),
                        RegisterSuccessResponse.class
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class);
    }
}
