package com.meg.listshop.configuration;

import com.meg.postoffice.config.MailConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


@ConfigurationProperties(prefix = "postoffice.test.mail")
public class MailPropertiesTestConfiguration {



    @Bean
    public MailConfiguration mailConfiguration() {

        return new MailConfiguration();
    }


}