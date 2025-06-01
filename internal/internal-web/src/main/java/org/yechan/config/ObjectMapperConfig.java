package org.yechan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
     @Bean
     @ConditionalOnMissingBean(ObjectMapper.class)
     public ObjectMapper objectMapper() {
         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.registerModule(new JavaTimeModule());
         objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
         return objectMapper;
     }
}
