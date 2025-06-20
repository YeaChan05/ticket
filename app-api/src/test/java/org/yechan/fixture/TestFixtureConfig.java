package org.yechan.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@TestConfiguration
public class TestFixtureConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TestFixture testFixture(TestRestTemplate testRestTemplate,
                                   ObjectMapper objectMapper) {
        return new TestFixture(testRestTemplate, objectMapper);
    }
}
