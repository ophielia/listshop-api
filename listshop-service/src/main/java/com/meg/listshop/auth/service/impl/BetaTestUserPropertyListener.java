package com.meg.listshop.auth.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.data.entity.UserPropertyEntity;
import com.meg.listshop.auth.service.UserPropertyChangeListener;
import com.meg.listshop.auth.service.UserPropertyKey;
import com.meg.listshop.auth.service.UserPropertyService;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
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

    private static final Logger  logger = LoggerFactory.getLogger(BetaTestUserPropertyListener.class);

    @Autowired
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
        parameters.addParameter("testLink", "https://testflight.apple.com/join/hXxCpVMY");

        try {
            mailService.processEmail(parameters);
            // update mail sent
            setEmailSentProperty(infoProperty.getUser());
        } catch (TemplateException | MessagingException | IOException e) {
            logger.warn(EMAIL_ERROR, infoProperty.getUser().getId(), e);
        } catch (BadParameterException e) {
            logger.warn("Error saving user property", e);
        }
    }

    private void setEmailSentProperty(UserEntity user) throws BadParameterException {
        UserPropertyEntity emailSentProperty = userPropertyService.getPropertyForUser(user.getUsername(), UserPropertyKey.TestEmailSent.getDisplayName());
        if (emailSentProperty == null) {
            emailSentProperty = new UserPropertyEntity();
            emailSentProperty.setUser(user);
            emailSentProperty.setKey(UserPropertyKey.TestEmailSent.getDisplayName());
            emailSentProperty.setValue("0");
        }

        int initialCount = Integer.parseInt(emailSentProperty.getValue());
        initialCount++;
        emailSentProperty.setValue(String.valueOf(initialCount));
        userPropertyService.setPropertiesForUser(user.getUsername(), Collections.singletonList(emailSentProperty), true);


    }
}
