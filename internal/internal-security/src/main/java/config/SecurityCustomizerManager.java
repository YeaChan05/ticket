package config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@ConditionalOnProperty(name = "security.extensible-enabled", havingValue = "true")
@RequiredArgsConstructor
class SecurityCustomizerManager {

    private final SecurityConfigurationProperties properties;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer,
            Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer,
            Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer,
            Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizationCustomizer,
            Customizer<HttpBasicConfigurer<HttpSecurity>> httpBasicCustomizer,
            Customizer<FormLoginConfigurer<HttpSecurity>> formLoginCustomizer,
            Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer,
            Customizer<LogoutConfigurer<HttpSecurity>> logoutCustomizer
    ) throws Exception {
        return http
                .csrf(csrfCustomizer)
                .cors(corsCustomizer)
                .sessionManagement(sessionManagementCustomizer)
                .authorizeHttpRequests(authorizationCustomizer)
                .httpBasic(httpBasicCustomizer)
                .formLogin(formLoginCustomizer)
                .exceptionHandling(exceptionHandlingCustomizer)
                .logout(logoutCustomizer)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "authorizationCustomizer")
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizationCustomizer() {
        return auth -> {
            properties.publicEndpoints().forEach(endpoint ->
                    auth.requestMatchers(endpoint).permitAll()
            );

            properties.userEndpoints().forEach(endpoint ->
                    auth.requestMatchers(endpoint).authenticated()
            );

            auth.anyRequest().denyAll();
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "csrfCustomizer")
    public Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "corsCustomizer")
    public Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "sessionManagementCustomizer")
    public Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer() {
        return session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    @ConditionalOnMissingBean(name = "httpBasicCustomizer")
    public Customizer<HttpBasicConfigurer<HttpSecurity>> httpBasicCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "formLoginCustomizer")
    public Customizer<FormLoginConfigurer<HttpSecurity>> formLoginCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "exceptionHandlingCustomizer")
    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlingCustomizer() {
        return AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "logoutCustomizer")
    public Customizer<LogoutConfigurer<HttpSecurity>> logoutCustomizer() {
        return logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true);
    }
}
