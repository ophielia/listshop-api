/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.service.config.ContentConfiguration;
import com.meg.postoffice.service.content.ContentBuilder;
import com.meg.postoffice.service.content.ContentBuilderFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@Service
@EnableConfigurationProperties(ContentConfiguration.class)
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ContentConfiguration contentConfiguration;
    private final Configuration configuration;

    private JavaMailSender javaMailSender;

    public MailService(ContentConfiguration contentConfiguration,
                       Configuration freeMarkerViewResolver,
                       JavaMailSender javaMailSender) {
        this.contentConfiguration = contentConfiguration;
        this.configuration = freeMarkerViewResolver;
        this.javaMailSender = javaMailSender;
    }

    public void processEmail(EmailParameters emailParameters) throws TemplateException, IOException, MessagingException {
        ContentBuilder contentBuilder = new ContentBuilderFactory(emailParameters, configuration).build();
        String content = contentBuilder.buildContent();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Welcome To SpringHow.com");
        helper.setTo("ophielia@yahoo.com");
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);

    }

    public String buildEmailContent() throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("test", this.contentConfiguration.getTest());
        configuration.getTemplate("email.ftlh").process(model, stringWriter);
        return stringWriter.toString();
    }
}
