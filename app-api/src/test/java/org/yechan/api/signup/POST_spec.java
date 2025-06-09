package org.yechan.api.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.api.fixture.UserFixture.generatePhone;
import static org.yechan.api.fixture.UserFixture.generateUserRegisterRequest;
import static org.yechan.api.fixture.UserFixture.generateUsername;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yechan.api.config.IntegrationTest;
import org.yechan.config.response.ApiResponse;
import org.yechan.config.response.ErrorResponse;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.entity.User;
import org.yechan.repository.JpaUserRepository;

@IntegrationTest
public class POST_spec {

    @Test
    void 올바른_정보로_요청하면_성공하고_SUCCESS_응답이_반환된다(
            @Autowired TestRestTemplate client,
            @Autowired ObjectMapper objectMapper
    ) {
        // Arrange
        UserRegisterRequest request = generateUserRegisterRequest();
        // Act
        var response = client.postForObject(
                "/api/v1/users/sign-up",
                request,
                ApiResponse.class
        );

        // Assert
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getData()).isNotNull();

        var responseData = objectMapper.convertValue(response.getData(), RegisterSuccessResponse.class);
        assertThat(responseData.name()).isEqualTo(request.name());
        assertThat(responseData.email()).isEqualTo(request.email());
    }

    @Test
    void 이미_존재하는_이메일로_요청하면_실패하고_400_Bad_Request_응답이_반환된다(
            @Autowired TestRestTemplate client,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired JpaUserRepository userRepository
    ) {
        // Arrange
        User existingUser = User.builder()
                .name("existingUser")
                .email("")
                .name("testUser")
                .password(passwordEncoder.encode("securep!21Assword"))
                .phone("010-1234-5678")
                .build();
        UserRegisterRequest request = generateUserRegisterRequest();
        userRepository.save(existingUser);

        UserRegisterRequest duplicatedEmailRequest = new UserRegisterRequest(
                generateUsername(),
                existingUser.getEmail(), // 중복된 이메일 사용
                request.password(),
                generatePhone()
        );

        // Act
        var response = client.postForObject(
                "/api/v1/users/sign-up",
                duplicatedEmailRequest,
                ErrorResponse.class
        );

        // Assert
        assertThat(response.getStatus()).isNotNull();
        assertThat(response.getStatus()).isEqualTo("EMAIL-001");
        assertThat(response.getMessage()).isEqualTo("이미 사용중인 이메일입니다.");
    }

    @ParameterizedTest
    @CsvSource({
            "testexample.com, 이메일 주소는 '@' 기호를 포함해야 합니다.",
            "test@.com, 이메일 형식이 올바르지 않습니다.",
            "test@com, 이메일 형식이 올바르지 않습니다.",
            "test@-example.com, 이메일 도메인 부분이 올바르지 않습니다.",
            "test@example..com, 이메일 주소에 연속된 점(.)이 포함되어 있습니다."
    })
    void 유효하지_않은_이메일_형식으로_요청하면_실패하고_400_Bad_Request_응답이_반환된다(
            String invalidEmail,
            String expectedMessage,
            @Autowired TestRestTemplate client) {
        // Arrange
        var request = generateUserRegisterRequest();
        UserRegisterRequest invalidRequest = new UserRegisterRequest(
                generateUsername(),
                invalidEmail,
                request.password(),
                generatePhone()
        );

        // Act
        var response = client.postForObject(
                "/api/v1/users/sign-up",
                invalidRequest,
                ErrorResponse.class
        );

        // Assert
        assertThat(response.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
        assertThat(response.getMessage()).isEqualTo(expectedMessage);
    }
}
