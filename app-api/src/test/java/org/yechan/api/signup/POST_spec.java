package org.yechan.api.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.EmailGenerator.generateEmail;
import static org.yechan.PhoneNumberGenerator.generatePhone;
import static org.yechan.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.TestFixture;
import org.yechan.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;

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
}
