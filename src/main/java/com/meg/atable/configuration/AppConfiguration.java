package com.meg.atable.configuration;

import com.meg.atable.service.ShoppingListProperties;
import org.h2.server.web.WebServlet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ShoppingListProperties.class)
public class AppConfiguration {

}