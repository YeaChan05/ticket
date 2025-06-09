package org.yechan.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

@TestConfiguration
public class TestRestTemplateConfig {

    @Bean
    @Lazy
    public TestRestTemplate testRestTemplate(Environment env, RestTemplateBuilder builder) {
        String port = env.getProperty("local.server.port");
        RestTemplateBuilder customizedBuilder = builder
                .rootUri("http://localhost:" + port);
        return new TestRestTemplate(customizedBuilder);
    }
}
