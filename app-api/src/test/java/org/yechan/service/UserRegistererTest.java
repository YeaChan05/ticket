package org.yechan.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import autoparams.AutoParams;
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
    @AutoParams
    void 검증된_값으로_이루어진_회원가입은_성공한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);

        // when
        RegisterSuccessResponse response = userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone));

        // then
        verify(userRepository).insertUser(name, email, passwordEncoder.encode(password), phone);
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    @AutoParams
    void 이미_존재하는_이메일로_회원가입을_시도하면_예외가_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");

    }

    @Test
    @AutoParams
    void 이미_존재하는_이름으로_회원가입을_시도하면_예외가_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with name " + name + " already exists.");
    }

    @Test
    @AutoParams
    void 이미_존재하는_전화번호로_회원가입을_시도하면_예외가_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with phone " + phone + " already exists.");
    }

    @Test
    @AutoParams
    void 세_예외상황이_모두_발생하는_상황의_경우_email_예외가_먼저_발생한다(String name, String email, String password, String phone) {
        // given
        lenient().when(userRepository.existsByEmail(email)).thenReturn(true);
        lenient().when(userRepository.existsByName(name)).thenReturn(true);
        lenient().when(userRepository.existsByPhone(phone)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(new UserRegisterRequest(name, email, password, phone)))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");
    }
}
