package org.yechan;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@TestConfiguration
public class TestFixtureConfig {

    @Bean
    @Scope("prototype")
    public TestFixture testFixture(TestRestTemplate testRestTemplate) {
        return new TestFixture(testRestTemplate);
    }
}
