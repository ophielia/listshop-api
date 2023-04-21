package com.meg.listshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.meg.listshop", "com.meg.postoffice"})
@EnableScheduling
public class Application {

    private static final Logger  LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
      //  SpringApplication springApplication = new SpringApplication(Application.class);
        // this is only for debugging properties
        // springApplication.addListeners(new PropertiesLogger());
        createSpringApplication().run(args);
    }

    public static SpringApplication createSpringApplication() {
        return new SpringApplication(Application.class);
    }

}
