package config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecurityConfigurationPropertiesTest {

    @Test
    @DisplayName("중복된 엔드포인트가 없을 때, 예외가 발생하지 않아야 한다.")
    void noConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/**");
        List<String> userEndpoints = List.of("/user/**");
        SecurityConfigurationProperties properties = initProperties(publicEndpoints, userEndpoints);

        // then
        assertThat(properties.userEndpoints()).doesNotContain(String.valueOf(publicEndpoints));

    }

    @Test
    @DisplayName("중복된 엔드포인트가 있을 때, 예외가 발생해야 한다.")
    void withConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/**");
        List<String> userEndpoints = List.of("/public/**");

        // when & then
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("엔드포인트가 비어있을 때, 예외가 발생하지 않아야 한다.")
    void emptyEndpoints() {
        // given
        List<String> publicEndpoints = List.of();
        List<String> userEndpoints = List.of();

        // when & then
        assertDoesNotThrow(() -> initProperties(publicEndpoints, userEndpoints));
    }

    private SecurityConfigurationProperties initProperties(final List<String> publicEndpoints,
                                                           final List<String> userEndpoints) {
        return new SecurityConfigurationProperties(
                publicEndpoints,
                userEndpoints,
                false,
                false,
                false
        );
    }
}
