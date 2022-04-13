/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.config;

import com.meg.postoffice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableConfigurationProperties({ContentConfiguration.class, MailConfiguration.class})
public class PostOfficeConfiguration {
    //MM autowired work
    @Autowired
    private ContentConfiguration contentConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    @Bean
    public MailService mailService() {
        return new MailService(contentConfiguration,
                freemarkerConfig().getConfiguration(),
                javaMailSender(),
                mailConfiguration.getTestDiversionEmail(),
                mailConfiguration.getSendingEnabled()
        );
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
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/emailtemplates");
        return freeMarkerConfigurer;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        if (mailConfiguration.getUsername() == null) {
            return mailSender;
        }
        mailSender.setHost(mailConfiguration.getHost());
        mailSender.setPort(mailConfiguration.getPort());


        mailSender.setUsername(mailConfiguration.getUsername());
        mailSender.setPassword(mailConfiguration.getPassword());

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailConfiguration.getProtocol());
        props.put("mail.smtp.auth", mailConfiguration.getSmtpAuth());
        props.put("mail.debug", mailConfiguration.getDebug());
        if (mailConfiguration.getEnableSSL()) {
            props.put("mail.smtp.ssl.enable", mailConfiguration.getEnableSSL());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.timeout", 25000);
        } else {
            props.put("mail.smtp.starttls.enable", mailConfiguration.getEnableStartTls());
        }

        return mailSender;
    }
}
