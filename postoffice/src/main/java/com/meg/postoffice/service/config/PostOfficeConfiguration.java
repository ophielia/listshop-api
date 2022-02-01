package com.meg.postoffice.service.config;

import com.meg.postoffice.service.MailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableConfigurationProperties(ContentConfiguration.class)
public class PostOfficeConfiguration {

    @Autowired
    private ContentConfiguration contentConfiguration;


    @Bean
    public MailServiceImpl mailService() {
        return new MailServiceImpl(contentConfiguration);
    }

    @Bean
    public ContentConfiguration contentConfiguration() {
        return new ContentConfiguration();
    }

    @Bean
    public FreeMarkerViewResolver freemarkerViewResolver() {

        var resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setSuffix(".ftlh");
        return resolver;
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {

        var freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/emailtemplates/");
        return freeMarkerConfigurer;
    }


}
