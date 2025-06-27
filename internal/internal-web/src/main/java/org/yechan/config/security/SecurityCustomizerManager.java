package org.yechan.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;


@Configuration
@RequiredArgsConstructor
class SecurityCustomizerManager {


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
            Customizer<LogoutConfigurer<HttpSecurity>> logoutCustomizer,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            OncePerRequestFilter authenticationFilter
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
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "authorizationCustomizer")
    @ConditionalOnProperty(name = "security.extensible-enabled", havingValue = "true")
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizationCustomizer(
            SecurityConfigurationProperties properties
    ) {
        return auth -> {
            properties.publicEndpoints().forEach(endpoint ->
                    auth.requestMatchers(endpoint).permitAll()
            );

            properties.userEndpoints().forEach(endpoint ->
                    auth.requestMatchers(endpoint).hasRole("USER")
            );

            properties.sellerEndpoints().forEach(endpoint ->
                    auth.requestMatchers(endpoint).hasRole("SELLER")
            );

            auth.anyRequest().denyAll();
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "csrfCustomizer")
    @ConditionalOnProperty(name = "security.extensible-enabled", havingValue = "true")
    public Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer(
            SecurityConfigurationProperties properties
    ) {
        return properties.csrfEnabled() ?
                Customizer.withDefaults() :
                AbstractHttpConfigurer::disable;
    }

    @Bean
    @ConditionalOnMissingBean(name = "corsCustomizer")
    @ConditionalOnProperty(name = "security.extensible-enabled", havingValue = "true")
    public Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer(
            SecurityConfigurationProperties properties
    ) {
        return properties.corsEnabled() ?
                Customizer.withDefaults() :
                AbstractHttpConfigurer::disable;
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

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean
    public OncePerRequestFilter authenticationFilter(AuthenticationManager authenticationManager) {
        return new BasicAuthenticationFilter(authenticationManager);
    }
}
