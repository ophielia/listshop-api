/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.configuration;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by margaretmartin on 04/10/2017.
 */
@Configuration
public class TestConfiguration {

    @Profile("test")
    @Bean
    public FlywayMigrationStrategy clean() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
