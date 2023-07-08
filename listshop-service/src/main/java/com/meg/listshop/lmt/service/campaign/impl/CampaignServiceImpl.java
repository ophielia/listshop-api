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
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final MailService mailService;

    public static final String BETA_RECEIVER = "meg@the-list-shop.com";
    public static final String BETA_SENDER = "support@the-list-shop.com";

    @Autowired
    public CampaignServiceImpl(CampaignRepository campaignRepository, MailService mailService) {
        this.campaignRepository = campaignRepository;
        this.mailService = mailService;
    }

    @Autowired


    @Override
    public void addCampaignEmail(String campaign, String email) {
        logger.info("Beginning Add Campaign Email: campaign[{}]", campaign);
        if (!StringTools.isEmail(email)) {
            logger.info("Not adding email - email does not appear to be valid[{}]", email);
            return;
        }
        // if entry already exists, return
        CampaignEntity existing = campaignRepository.findByEmailAndCampaign(email, campaign);
        if (existing != null) {
            logger.info("Not adding email - since it already exists [{}]", email.substring(3, 4));
            return;
        }
        // create entry
        CampaignEntity newEntity = new CampaignEntity(campaign, email);
        newEntity = campaignRepository.save(newEntity);
        // send notification email
        sendEmail(newEntity);

    }

    private void sendEmail(CampaignEntity campaign) {
        // send email
        EmailParameters parameters = new EmailParameters();
        parameters.setEmailType(EmailType.BetaNotification);
        parameters.setReceiver(BETA_RECEIVER);
        parameters.setSender(BETA_SENDER);
        parameters.setSubject("Interest in Beta");
        parameters.addParameter("userEmail", campaign.getEmail());
        parameters.addParameter("campaign", campaign.getCampaign());

        try {
            mailService.processEmail(parameters);
        } catch (TemplateException | MessagingException | IOException e) {
            logger.warn("Cant send notification email for campaign [{}]", campaign, e);
        }
    }
}
