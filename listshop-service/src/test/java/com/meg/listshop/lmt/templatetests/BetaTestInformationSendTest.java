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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import java.io.IOException;

@TestPropertySource(locations = "/mytestsend.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PostofficeTestConfiguration.class})
public class BetaTestInformationSendTest {

    @Autowired
    private MailService mailService;


    @Test
    public void testSendBetaTestInfo() throws TemplateException, IOException, MessagingException {
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.BetaTestInformation);
        parameters.setReceiver("margaret.martin@orange.fr");
        parameters.setSender("mophielia@gmail.com");
        parameters.setSubject("The List Shop - testing information");
        parameters.addParameter("staticRoot", "https://nastyvarmits.fr/api/static");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");
        parameters.addParameter("testLink", "http://localhost:4200/home");

        mailService.processEmail(parameters);
    }

}