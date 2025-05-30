package org.yechan.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"org.yechan.entity"})
public class EntityManagementConfig {
}
