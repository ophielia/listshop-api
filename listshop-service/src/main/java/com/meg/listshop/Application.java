package com.meg.listshop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.meg.listshop", "com.meg.postoffice"})
@EnableScheduling
public class Application {

    private static final Logger LOG = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        // this is only for debugging properties
        // springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);

/*
            LOG.info(
                "Application '{}' version '{}' (build '{}') is running with Profile(s): {}",
                env.getProperty("artifactId"),
                env.getProperty("info.app.version"),
                env.getProperty("info.app.build"),
                env.getActiveProfiles()
        );

 */
    }



}
