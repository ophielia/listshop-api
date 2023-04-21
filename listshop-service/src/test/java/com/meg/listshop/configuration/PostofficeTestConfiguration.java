package com.meg.listshop.configuration;

import com.meg.postoffice.config.MailConfiguration;
import com.meg.postoffice.service.MailService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@TestConfiguration
public class PostofficeTestConfiguration {

    @Bean
    public FreeMarkerConfigurer freemarkerClassLoaderConfig() {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/emailtemplates");
        configuration.setTemplateLoader(templateLoader);
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setConfiguration(configuration);
        return freeMarkerConfigurer;
    }

    @Bean
    @ConfigurationProperties(prefix = "postoffice.mail")
    public MailConfiguration mailConfiguration() {
        return new MailConfiguration();
    }

    @Bean
    @Autowired
    public JavaMailSender javaMailSender(MailConfiguration mailConfiguration) {

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

    @Bean
    @Autowired
    public MailService mailService(FreeMarkerConfigurer freemarkerConfigurer, JavaMailSender javaMailSender, MailConfiguration mailConfiguration) {
        return new MailService( freemarkerConfigurer, javaMailSender, mailConfiguration);
    }
}