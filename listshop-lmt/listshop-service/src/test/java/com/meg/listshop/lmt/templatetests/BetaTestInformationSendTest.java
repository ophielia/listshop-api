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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import jakarta.mail.MessagingException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;


@TestPropertySource(locations = "/mytestsend.properties")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PostofficeTestConfiguration.class})
class BetaTestInformationSendTest {

    @Autowired
    private MailService mailService;


    @Test
    void testSendBetaTestInfo() throws TemplateException, IOException, MessagingException {
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.BetaTestInformation);
        parameters.setReceiver("margaret.martin@orange.fr");
        parameters.setSender("mophielia@gmail.com");
        parameters.setSubject("The List Shop - testing information");
        parameters.addParameter("staticRoot", "https://static.nastyvarmits.fr");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");
        parameters.addParameter("testLink", "http://localhost:4200/home");

        mailService.processEmail(parameters);
    }

}
