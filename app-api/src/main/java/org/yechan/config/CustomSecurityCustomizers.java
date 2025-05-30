package org.yechan.config;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CustomSecurityCustomizers {

    /**
     * 개발 환경용 CORS 설정 - 모든 Origin 허용
     */
    @Configuration
    @Profile("local")
    @ConditionalOnProperty(name = "security.cors-enabled", havingValue = "true")
    static class DevelopmentCorsConfig {
        
        @Bean
        public Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
            return cors -> cors.configurationSource(corsConfigurationSource());
        }

        @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
-       configuration.setAllowedOriginPatterns(Arrays.asList("*"));
+       configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
-       configuration.setAllowCredentials(true);
+       configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    }

    /**
     * CSRF 토큰 기반 보안이 필요한 환경용
     */
    @Configuration
    @ConditionalOnProperty(name = "security.csrf-enabled", havingValue = "true")
    static class CsrfEnabledConfig {
        
        @Bean
        public Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer() {
            return csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    }
}
