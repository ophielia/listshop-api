package com.meg.listshop.configuration;

import org.testcontainers.containers.PostgreSQLContainer;

public class ListShopPostgresqlContainer extends PostgreSQLContainer<ListShopPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:10.14";
    private static ListShopPostgresqlContainer container;

    ListShopPostgresqlContainer() {
        super(IMAGE_VERSION);
    }


    public static ListShopPostgresqlContainer getInstance() {
        if (container == null) {
            container = new ListShopPostgresqlContainer()
                    .withUsername("bankuser")
                    .withPassword("bankuser")
                    .withInitScript("db/init.sql");
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}