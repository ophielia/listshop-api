/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.config.ContentConfiguration;
import com.meg.postoffice.service.content.ContentBuilderFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


//@Service
@EnableConfigurationProperties(ContentConfiguration.class)
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private final ContentConfiguration contentConfiguration;
    private final Configuration configuration;
    private final String testDiversionEmail;
    private final boolean sendingEnabled;

    private JavaMailSender javaMailSender;

    public MailService(ContentConfiguration contentConfiguration,
                       Configuration freeMarkerViewResolver,
                       JavaMailSender javaMailSender,
                       String testDiversionEmail,
                       Boolean sendingEnabled) {
        this.contentConfiguration = contentConfiguration;
        this.configuration = freeMarkerViewResolver;
        this.javaMailSender = javaMailSender;
        this.testDiversionEmail = testDiversionEmail;
        this.sendingEnabled = sendingEnabled != null ? sendingEnabled : false;
    }

    public void processEmail(EmailParameters emailParameters) throws TemplateException, IOException, MessagingException {
        var contentBuilder = new ContentBuilderFactory(emailParameters, configuration).build();
        String content = contentBuilder.buildContent();
        LOG.debug("Content created: {}", content);

        var subject = getEmailSubject(emailParameters);
        var recipient = getEmailRecipient(emailParameters);
        var mimeMessage = javaMailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(new InternetAddress("support@the-list-shop.com", "List Shop Support"));

        helper.setSubject(subject);
        helper.setTo(recipient);
        helper.setText(content, true);

        if (sendingEnabled) {
            javaMailSender.send(mimeMessage);
        }

    }

    private String getEmailRecipient(EmailParameters emailParameters) {
        if (testDiversionEmail != null) {
            return testDiversionEmail;
        }
        return emailParameters.getReceiver();
    }

    private String getEmailSubject(EmailParameters emailParameters) {
        if (testDiversionEmail != null) {
            return String.format("(%s) %s", emailParameters.getReceiver(), emailParameters.getSubject());
        }
        return emailParameters.getSubject();
    }

    public String buildEmailContent() throws IOException, TemplateException {
        var stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("test", this.contentConfiguration.getTest());
        configuration.getTemplate("email.ftlh").process(model, stringWriter);
        return stringWriter.toString();
    }
}
