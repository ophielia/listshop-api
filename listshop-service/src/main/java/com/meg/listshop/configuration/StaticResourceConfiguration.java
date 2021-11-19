package com.meg.listshop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by margaretmartin on 04/10/2017.
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Value("${listshop.static.resources.location}")
    private String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(resourceLocation);
    }


}
