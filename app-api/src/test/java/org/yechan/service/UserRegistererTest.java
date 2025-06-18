package org.yechan.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.UserException;

@ExtendWith(MockitoExtension.class)
class UserRegistererTest {
    @InjectMocks
    private UserRegisterer userRegisterer;

    @Mock
    private UserPersister userPersister;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 검증된_값으로_이루어진_회원가입은_성공한다() {
        // Arrange
        String name = generateUsername();
        String email = generateEmail();
        String password = "Password123!";
        String phone = generatePhone();
        UserRegisterRequest user = new UserRegisterRequest(name, email, password, phone);

        // Act
        doNothing().when(userValidator).validateEmailUniqueness(anyString());
        doNothing().when(userValidator).validateUsernameUniqueness(anyString());
        doNothing().when(userValidator).validatePhoneNumberUniqueness(anyString());
        lenient().when(passwordEncoder.encode(password)).thenReturn("ENCODED_" + password);

        RegisterSuccessResponse response = userRegisterer.registerUser(user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo(name);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    void 이미_존재하는_이메일로_회원가입을_시도하면_예외가_발생한다() {
        // Arrange
        String name = generateUsername();
        String email = generateEmail();
        String password = "Password123!";
        String phone = generatePhone();
        doThrow(
                new UserException("User with email " + email + " already exists.", UserErrorCode.EMAIL_DUPLICATED)
        ).when(userValidator).validateEmailUniqueness(anyString());

        // Act & Assert
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");

    }

    @Test
    void 이미_존재하는_이름으로_회원가입을_시도하면_예외가_발생한다() {
        // Arrange
        String name = generateUsername();
        String email = generateEmail();
        String password = "Password123!";
        String phone = generatePhone();
        doNothing().when(userValidator).validateEmailUniqueness(anyString());
        doThrow(
                new UserException("User with username " + name + " already exists.", UserErrorCode.NAME_DUPLICATED)
        ).when(userValidator).validateUsernameUniqueness(anyString());

        // Act & Assert
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with username " + name + " already exists.");
    }

    @Test
    void 이미_존재하는_전화번호로_회원가입을_시도하면_예외가_발생한다() {
        // Arrange
        String name = generateUsername();
        String email = generateEmail();
        String password = "Password123!";
        String phone = generatePhone();
        lenient().doNothing().when(userValidator).validateEmailUniqueness(anyString());
        lenient().doNothing().when(userValidator).validateUsernameUniqueness(anyString());
        lenient().doThrow(
                new UserException("User with phone " + phone + " already exists.", UserErrorCode.PHONE_DUPLICATED)
        ).when(userValidator).validatePhoneNumberUniqueness(anyString());

        // Act & Assert
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with phone " + phone + " already exists.");
    }

    @Test
    void 세_예외상황이_모두_발생하는_상황의_경우_email_예외가_먼저_발생한다() {
        // Arrange
        String name = generateUsername();
        String email = generateEmail();
        String password = "Password123!";
        String phone = generatePhone();
        lenient().doThrow(new UserException("User with email " + email + " already exists.", UserErrorCode.EMAIL_DUPLICATED))
                .when(userValidator).validateEmailUniqueness(email);
        lenient().doThrow(new UserException("User with username " + name + " already exists.", UserErrorCode.NAME_DUPLICATED))
                .when(userValidator).validateUsernameUniqueness(name);
        lenient().doThrow(new UserException("User with phone " + phone + " already exists.", UserErrorCode.PHONE_DUPLICATED))
                .when(userValidator).validatePhoneNumberUniqueness(password);

        // Act & Assert
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");
    }
}
