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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@Import(PostofficeTestConfiguration.class)
@TestPropertySource(locations = "/mytestsend.properties")
public class ResetPasswordSendTest {

    @Autowired
    private MailService mailService;


    @Test
    public void testResetPassword() throws TemplateException, IOException, MessagingException {
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.ResetPassword);
        parameters.setReceiver("margaret.martin@orange.fr");
        parameters.setSender("mophielia@gmail.com");
        parameters.setSubject("Password Reset");
        parameters.addParameter("staticRoot", "https://nastyvarmits.fr/api/static");
        parameters.addParameter("tokenLink", "http://localhost:4200/home");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");

        mailService.processEmail(parameters);
        Assert.assertTrue(1 == 1);
    }

}