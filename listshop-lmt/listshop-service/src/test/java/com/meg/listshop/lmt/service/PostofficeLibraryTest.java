/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.service;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.MessagingException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Testcontainers
@ActiveProfiles("test")
class PostofficeLibraryTest {

    @Autowired
    MailService mailService;

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Test
    void testMailService() throws TemplateException, IOException, MessagingException {
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.ResetPassword);
        parameters.setReceiver("margaret.martin@orange.fr");
        parameters.setSender("sender");
        parameters.setSubject("subject");
        parameters.addParameter("staticRoot", "https://nastyvarmits.fr/api/static");
        parameters.addParameter("tokenLink", "http://localhost:4200/home");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");

        mailService.processEmail(parameters);
    }
}
