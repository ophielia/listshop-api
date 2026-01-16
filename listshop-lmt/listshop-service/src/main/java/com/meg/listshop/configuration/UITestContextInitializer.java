package com.meg.listshop.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

public abstract class UITestContextInitializer {

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static PostgreSQLContainer<UITestPostgresqlContainer> postgres = new UITestPostgresqlContainer()
                .withUsername("bankuser")
                .withPassword("bankuser")
                .withInitScript("db/uitest/init.sql");

        public static Map<String, String> getProperties() {
            Startables.deepStart(Stream.of( postgres)).join();

            return Map.of(
                    "spring.datasource.url", postgres.getJdbcUrl(),
                    "spring.datasource.username", postgres.getUsername(),
                    "spring.datasource.password",postgres.getPassword()
            );
        }

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            var env = context.getEnvironment();
            env.getPropertySources().addFirst(new MapPropertySource(
                    "test",
                    (Map) getProperties()
            ));
        }
    }
}
