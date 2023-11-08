package com.meg.listshop.configuration;


import org.testcontainers.containers.PostgreSQLContainer;

public class UITestPostgresqlContainer extends PostgreSQLContainer<UITestPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:10.14";
    private static UITestPostgresqlContainer container;

    UITestPostgresqlContainer() {
        super(IMAGE_VERSION);
    }



    public static UITestPostgresqlContainer postgreSQLContainer = UITestPostgresqlContainer.getInstance();

    public static UITestPostgresqlContainer getInstance() {
        if (container == null) {
            container = new UITestPostgresqlContainer()
                    .withUsername("bankuser")
                    .withPassword("bankuser")
                    .withInitScript("db/init.sql");
        }
        return container;
    }


}