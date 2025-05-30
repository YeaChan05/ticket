package org.yechan.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Internal Security 모듈의 Auto Configuration
 */
@AutoConfiguration
@ConditionalOnClass(HttpSecurity.class)
@EnableConfigurationProperties(SecurityConfigurationProperties.class)
@ConfigurationPropertiesScan("config")
@Import({
    SecurityCustomizerManager.class,
})
public class SecurityAutoConfiguration {
}
