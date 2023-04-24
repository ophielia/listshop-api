/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service;

import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.config.MailConfiguration;
import com.meg.postoffice.service.content.ContentBuilderFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;


//@Service
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private final Configuration configuration;
    private final String testDiversionEmail;
    private final boolean sendingEnabled;

    private JavaMailSender javaMailSender;

    public MailService(FreeMarkerConfigurer freeMarkerConfigurer,
                       JavaMailSender javaMailSender,
                       MailConfiguration mailConfiguration) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
        this.javaMailSender = javaMailSender;
        this.testDiversionEmail = mailConfiguration.getTestDiversionEmail();
        this.sendingEnabled = mailConfiguration.getSendingEnabled();
    }

    public void processEmail(EmailParameters emailParameters) throws TemplateException, IOException, MessagingException {
        LOG.info("Begin processEmail: emailType[{}], recipient[{}]", emailParameters.getEmailType(), emailParameters.getReceiver().substring(0, 5));
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
            LOG.debug("Email successfully sent");
        } else {
            LOG.info("Email processed, but not sent, due to configuration");
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


}
