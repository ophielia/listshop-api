package com.meg.listshop.lmt.service.campaign.impl;

import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.data.entity.CampaignEntity;
import com.meg.listshop.lmt.data.repository.CampaignRepository;
import com.meg.listshop.lmt.service.campaign.CampaignService;
import com.meg.postoffice.api.model.EmailParameters;
import com.meg.postoffice.api.model.EmailType;
import com.meg.postoffice.service.MailService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.io.IOException;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final MailService mailService;

    @Value("${campaigns.log.only:false}")
    private boolean logOnly;

    public static final String BETA_RECEIVER = "meg@the-list-shop.com";
    public static final String BETA_SENDER = "support@the-list-shop.com";

    @Autowired
    public CampaignServiceImpl(CampaignRepository campaignRepository, MailService mailService) {
        this.campaignRepository = campaignRepository;
        this.mailService = mailService;
    }


    @Override
    public void addCampaignEmail(String campaign, String email, String text) {
        logger.info("Beginning Add Campaign Email: campaign[{}]", campaign);

        // send notification email or log
        logger.info("Logging request for beta info [{}]", campaign);
        if (logOnly) {
            return;
        }
        sendEmail(email, campaign, text);

    }

    private void sendEmail(String email, String campaign, String text) {
        var contentEmailAddress = StringTools.fillIfEmpty(email, "---");
        // send email
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.BetaNotification);
        parameters.setReceiver(BETA_RECEIVER);
        parameters.setSender(BETA_SENDER);
        parameters.setSubject("Feedback from Beta");
        parameters.addParameter("userEmail", contentEmailAddress);
        parameters.addParameter("campaign", campaign);
        parameters.addParameter("feedback", text);

        try {
            mailService.processEmail(parameters);
        } catch (TemplateException | MessagingException | IOException e) {
            logger.warn("Cant send notification email for campaign [{}]", campaign, e);
        }
    }
}
