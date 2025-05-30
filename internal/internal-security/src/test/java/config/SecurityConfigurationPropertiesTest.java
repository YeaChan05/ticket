package config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yechan.config.SecurityConfigurationProperties;

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

    @Test
    @DisplayName("동일 권한에 대해 중복된 엔드포인트가 있더라도 예외가 발생하지 않는다.")
    void sameAuthorityNoConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/**", "/public/**");
        List<String> userEndpoints = List.of("/user/**");

        // when & then
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("서로 다른 권한의 엔드포인트에 와일드카드를 고려한 중복이 있을 때, 예외가 발생해야 한다.")
    void wildcardConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/**");
        List<String> userEndpoints = List.of("/public/user/**");

        // when & then
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("중간 와일드카드를 고려한 중복이 있을 때, 예외가 발생해야 한다.")
    void wildcardMiddleConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/user/**/test");
        List<String> userEndpoints = List.of("/public/**/test");

        // when & then
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("와일드카드가 없는 엔드포인트에 와일드카드를 고려한 중복이 있을 때, 예외가 발생해야 한다.")
    void wildcardNoConflicts() {
        // given
        List<String> publicEndpoints = List.of("/public/aaa/test");
        List<String> userEndpoints = List.of("/public/**/test");

        // when & then
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("같은 리스트 내에서 와일드카드 패턴 충돌시 예외 발생")
    void sameListWildcardConflict() {
        List<String> publicEndpoints = List.of("/public/**", "/public/abc/**");
        List<String> userEndpoints = List.of();
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증/비인증 리스트 간 포괄 패턴(/** 포함) 충돌시 예외 발생")
    void crossListSuperPatternConflict() {
        List<String> publicEndpoints = List.of("/api/**");
        List<String> userEndpoints = List.of("/api/user/profile");
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 패턴이나 결과적으로 같은 URL 매칭 - 예외 발생")
    void logicallyEqualPatternConflict() {
        List<String> publicEndpoints = List.of("/test/*/foo");
        List<String> userEndpoints = List.of("/test/**/foo");
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("경로가 겹치지 않으면 예외 발생하지 않음")
    void nonOverlappingPatternsNoConflict() {
        List<String> publicEndpoints = List.of("/public/**");
        List<String> userEndpoints = List.of("/user/**");
        assertDoesNotThrow(() -> initProperties(publicEndpoints, userEndpoints));
    }

    @Test
    @DisplayName("빈 패턴/루트 경로 충돌")
    void rootPatternConflict() {
        List<String> publicEndpoints = List.of("/**");
        List<String> userEndpoints = List.of("/user/**");
        assertThatThrownBy(() -> initProperties(publicEndpoints, userEndpoints))
                .isInstanceOf(IllegalArgumentException.class);
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
