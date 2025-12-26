/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.templatetests;

import com.meg.listshop.configuration.PostofficeTestConfiguration;
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.mail.MessagingException;
import java.io.IOException;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/mytestsend.properties")
@ContextConfiguration(classes = {PostofficeTestConfiguration.class})
class ResetPasswordSendTest {

    @Autowired
    private MailService mailService;


    @Test
    void testResetPassword() throws TemplateException, IOException, MessagingException {
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.ResetPassword);
        parameters.setReceiver("margaret.martin@orange.fr");
        parameters.setSender("mophielia@gmail.com");
        parameters.setSubject("Password Reset");
        parameters.addParameter("staticRoot", "https://static.nastyvarmits.fr");
        parameters.addParameter("tokenLink", "http://localhost:4200/home");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");

        mailService.processEmail(parameters);
        Assertions.assertTrue(1 == 1);
    }

}
