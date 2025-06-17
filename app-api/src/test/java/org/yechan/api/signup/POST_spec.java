package org.yechan.api.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PasswordGenerator.generatePassword;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yechan.config.IntegrationTest;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.fixture.TestFixture;
import org.yechan.repository.JpaUserRepository;

@IntegrationTest
@DisplayName("POST /api/v1/users/sign-up")
public class POST_spec {

    @Test
    void 회원가입_요청은_성공적으로_처리된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var username = generateUsername();
        var email = generateEmail();
        var phone = generatePhone();
        var request = new UserRegisterRequest(
                username,
                email,
                "Password123!",
                phone
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class)
                .onSuccess(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                        }
                );
    }

    @Test
    void 회원가입_성공_시_정의된_성공_코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange

        var username = generateUsername();
        var email = generateEmail();
        var phone = generatePhone();
        var request = new UserRegisterRequest(
                username,
                email,
                "Password123!",
                phone
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onSuccess(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                        }
                );

    }

    @Test
    void 비밀번호는_안전하게_암호화되어_데이터베이스에_저장된다(
            @Autowired TestFixture fixture,
            @Autowired JpaUserRepository repository,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        // Arrange
        var username = generateUsername();
        var email = generateEmail();
        var phone = generatePhone();
        var password = generatePassword();
        var request = new UserRegisterRequest(
                username,
                email,
                password,
                phone
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class)
                .onSuccess(
                        response -> {
                            var user = repository.findByEmail(email).orElseThrow();
                            assertThat(user.getPassword()).isNotEqualTo(password);
                            assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
                        }
                );
    }

    @Test
    void 회원_정보는_요청된_내용과_동일하게_데이터베이스에_저장된다(
            @Autowired TestFixture fixture,
            @Autowired JpaUserRepository repository
    ) {
        // Arrange
        var username = generateUsername();
        var email = generateEmail();
        var phone = generatePhone();
        var password = generatePassword();
        var request = new UserRegisterRequest(
                username,
                email,
                password,
                phone
        );
        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onSuccess(
                        response -> {
                            // Assert
                            var user = repository.findByEmail(email).orElseThrow();
                            assertThat(user.getName()).isEqualTo(username);
                            assertThat(user.getEmail()).isEqualTo(email);
                            assertThat(user.getPhone()).isEqualTo(phone);
                        }
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@domain..com",
    })
    void 이메일이_RFC_5322_형식을_만족하지_않은_경우_CONSTRAINT_VIOLATION이_응답된다(
            String email,
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var request = new UserRegisterRequest(
                generateUsername(),
                email,
                "Password123!",
                generatePhone()
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                        }
                );
    }

    @Test
    void 중복된_이메일로_가입_시_에러_코드_USER_006를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();
        var registeredRequest = new UserRegisterRequest(
                generateUsername(),
                email,
                "Password123!",
                generatePhone()
        );

        var request = new UserRegisterRequest(
                generateUsername(),
                email,
                "Qweasd123!",
                generatePhone()
        );
        fixture.post(
                        "/api/v1/users/sign-up",
                        registeredRequest
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class);

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("USER-006");
                        }
                );
    }

    @Test
    void 중복된_이름으로_가입_시_에러_코드_USER_002를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var username = generateUsername();
        var registeredRequest = new UserRegisterRequest(
                username,
                generateEmail(),
                "Password123!",
                generatePhone()
        );
        var request = new UserRegisterRequest(
                username,
                generateEmail(),
                "Qweasd123!",
                generatePhone()
        );
        fixture.post(
                        "/api/v1/users/sign-up",
                        registeredRequest
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class);

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("USER-002");
                        }
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short",
            "1234567",
            "abcdefgh",
            "ABCDEFGH",
            "12345678",
            "!@#$%^&*",
            "abc123!@#"
    })
    void 비밀번호는_최소_8자_이상_대문자_소문자_숫자_특수문자를_포함해야_한다(
            String password,
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var request = new UserRegisterRequest(
                generateUsername(),
                generateEmail(),
                password,
                generatePhone()
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                            assertThat(response.getMessage()).isEqualTo("비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.");
                        }
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",
            "abcdefghij",
            "ABCDEFGHIJ",
            "!@#$%^&*()",
            "abc123!@#"
    })
    @DisplayName("연락처는 010-XXXX-XXXX 형식이어야 한다")
    void 연락처는_010_XXXX_XXXX_형식이어야_한다(
            String phone,
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var request = new UserRegisterRequest(
                generateUsername(),
                generateEmail(),
                "Password123!",
                phone
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                            assertThat(response.getMessage()).isEqualTo("전화번호 형식은 010-xxxx-xxxx이어야 합니다.");
                        }
                );
    }

    @Test
    @DisplayName("중복된 연락처로 가입 시 에러 코드 USER-005를 반환한다")
    void 중복된_연락처로_가입_시_에러_코드_USER_005를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var phone = generatePhone();
        var registeredRequest = new UserRegisterRequest(
                generateUsername(),
                generateEmail(),
                "Password123!",
                phone
        );
        var request = new UserRegisterRequest(
                generateUsername(),
                generateEmail(),
                "Qweasd123!",
                phone
        );

        fixture.post(
                        "/api/v1/users/sign-up",
                        registeredRequest,
                        RegisterSuccessResponse.class
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class);

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request,
                        RegisterSuccessResponse.class
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("USER-005");
                        }
                );
    }

    static UserRegisterRequest[] invalidUsernameProvider() {
        return new UserRegisterRequest[]{
                new UserRegisterRequest(null, generateEmail(), "Password123!", generatePhone()),
                new UserRegisterRequest("", generateEmail(), "Password123!", generatePhone()),
                new UserRegisterRequest(generateUsername(), null, "Password123!", generatePhone()),
                new UserRegisterRequest(generateUsername(), "", "Password123!", generatePhone()),
                new UserRegisterRequest(generateUsername(), generateEmail(), null, generatePhone()),
                new UserRegisterRequest(generateUsername(), generateEmail(), "", generatePhone()),
                new UserRegisterRequest(generateUsername(), generateEmail(), "Password123!", null),
                new UserRegisterRequest(generateUsername(), generateEmail(), "Password123!", "")
        };
    }

    @ParameterizedTest
    @MethodSource("org.yechan.api.signup.POST_spec#invalidUsernameProvider")
    void 회원가입_페이지는_모든_필수_입력_필드를_포함한다(
            UserRegisterRequest request,
            @Autowired TestFixture fixture
    ) {
        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class)
                .onError(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                        }
                );
    }

    @Test
    void 회원가입_성공_시_사용자에게_명확한_완료_피드백을_제공한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var username = generateUsername();
        var email = generateEmail();
        var request = new UserRegisterRequest(
                username,
                email,
                "Password123!",
                generatePhone()
        );

        // Act
        fixture.post(
                        "/api/v1/users/sign-up",
                        request
                )
                .withoutToken()
                .exchange(RegisterSuccessResponse.class).onSuccess(
                        response -> {
                            // Assert
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                            assertThat(response.getData().email()).isEqualTo(email);
                            assertThat(response.getData().username()).isEqualTo(username);
                        }
                );
    }
}
