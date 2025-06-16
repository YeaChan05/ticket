package org.yechan.api.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.EmailGenerator.generateEmail;
import static org.yechan.PasswordGenerator.generatePassword;
import static org.yechan.PhoneNumberGenerator.generatePhone;
import static org.yechan.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yechan.TestFixture;
import org.yechan.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
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
        ApiResponse<RegisterSuccessResponse> response = fixture.post(
                "/api/v1/users/sign-up",
                request,
                RegisterSuccessResponse.class
        );

        // Assert
        var data = response.getData();
        assertThat(data.email()).isEqualTo(email);
        assertThat(data.username()).isEqualTo(username);
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
        var response = fixture.post(
                "/api/v1/users/sign-up",
                request,
                RegisterSuccessResponse.class
        );

        // Assert
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
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
                request,
                RegisterSuccessResponse.class
        );

        // Assert
        repository.findByEmail(email)
                .ifPresent(user -> {
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
                request,
                RegisterSuccessResponse.class
        );

        // Assert
        repository.findByEmail(email)
                .ifPresent(user -> {
                    assertThat(user.getName()).isEqualTo(username);
                    assertThat(user.getEmail()).isEqualTo(email);
                    assertThat(user.getPhone()).isEqualTo(phone);
                });
    }

}
