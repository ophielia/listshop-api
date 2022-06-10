package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.service.UserPropertyChangeListener;
import com.meg.listshop.auth.service.UserPropertyKey;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class BetaTestUserPropertyListener implements UserPropertyChangeListener {


    private static final String EMAIL_ERROR = "Unable to send email for property update, user[{}]";
    private UserPropertyService userPropertyService;

    private MailService mailService;
    @Value("${listservice.email.sender:support@the-list-shop.com}")
    String EMAIL_SENDER;

    private static final Logger logger = LogManager.getLogger(BetaTestUserPropertyListener.class);

    @Autowired
    public BetaTestUserPropertyListener(UserPropertyService userPropertyService) {
        this.userPropertyService = userPropertyService;
    }

    public BetaTestUserPropertyListener(UserPropertyService userPropertyService, MailService mailService) {
        this.userPropertyService = userPropertyService;
        this.mailService = mailService;
    }

    @PostConstruct
    public void init() {
        userPropertyService.addUserPropertyChangeListener(this);
    }


    @Override
    public void onPropertyUpdate(List<UserPropertyEntity> savedProperties) {
        // only intereste if we have the request info property
        UserPropertyEntity infoProperty = savedProperties.stream()
                .filter(p -> p.getKey().equals(UserPropertyKey.TestInfoRequested.getDisplayName()))
                .findFirst()
                .orElse(null);

        if (infoProperty == null) {
            return;
        }

        // gather information
        String userEmail = infoProperty.getUser().getUsername();

        // send email
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.BetaTestInformation);
        parameters.setReceiver(userEmail);
        parameters.setSender(EMAIL_SENDER);
        parameters.setSubject("Testing Information");
        parameters.addParameter("staticRoot", "https://nastyvarmits.fr/api/static");
        parameters.addParameter("supportEmail", "support@the-list-shop.com");
        parameters.addParameter("testLink", "http://localhost:4200/home");

        try {
            mailService.processEmail(parameters);
        } catch (TemplateException | MessagingException | IOException e) {
            logger.warn(EMAIL_ERROR, infoProperty.getUser().getId(), e);
        }
    }
}
