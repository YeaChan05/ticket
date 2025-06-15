package org.yechan.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.yechan.EmailGenerator.generateEmail;
import static org.yechan.PhoneNumberGenerator.generatePhone;
import static org.yechan.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserRegistererTest {
    @InjectMocks
    private UserRegisterer userRegisterer;

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        RegisterSuccessResponse response = userRegisterer.registerUser(user);

        // Assert
        verify(userRepository).insertUser(name, email, passwordEncoder.encode(password), phone);
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
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

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
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(true);

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
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(true);

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
        lenient().when(userRepository.existsByEmail(email)).thenReturn(true);
        lenient().when(userRepository.existsByName(name)).thenReturn(true);
        lenient().when(userRepository.existsByPhone(phone)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");
    }
}
