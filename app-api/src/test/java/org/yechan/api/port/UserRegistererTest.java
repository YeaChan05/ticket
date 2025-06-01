package org.yechan.api.port;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import autoparams.AutoParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yechan.dto.response.RegisterSuccessResponse;
import org.yechan.error.exception.UserException;
import org.yechan.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserRegistererTest {
    @InjectMocks
    private UserRegisterer userRegisterer;

    @Mock
    private UserRepository userRepository;

    @Test
    @AutoParams
    void 검증된_값으로_이루어진_회원가입은_성공한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existByName(anyString())).thenReturn(false);

        // when
        RegisterSuccessResponse response = userRegisterer.registerUser(name, email, password, phone);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @AutoParams
    void 이미_존재하는_이메일로_회원가입을_시도하면_예외가_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(name, email, password, phone))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");

    }

    @Test
    @AutoParams
    void 이미_존재하는_이름으로_회원가입을_시도하면_예외가_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existByName(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(name, email, password, phone))
                .isInstanceOf(UserException.class)
                .hasMessage("User with name " + name + " already exists.");
    }

    @Test
    @AutoParams
    void 두_예외상황이_둘다_발생하는_상황의_경우_email_예외가_먼저_발생한다(String name, String email, String password, String phone) {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.existByName(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerUser(name, email, password, phone))
                .isInstanceOf(UserException.class)
                .hasMessage("User with email " + email + " already exists.");
    }
}
