package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.CampaignControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.CampaignPut;
import com.meg.listshop.lmt.service.campaign.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@CrossOrigin
public class CampaignRestController implements CampaignControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(CampaignRestController.class);

    private final CampaignService campaignService;

    @Autowired
    CampaignRestController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }


    @Override
    public ResponseEntity<Object> addCampaign(HttpServletRequest request, Principal principal, CampaignPut input) throws BadParameterException {
        logger.info("Received new campaign: [{}]", input);
        // check email length
        String email = checkTextInput(input.getEmail());
        String campaign = checkTextInput(input.getCampaign());

        // send off to the service
        campaignService.addCampaignEmail(campaign, email);
        return ResponseEntity.noContent().build();
    }

    private String checkTextInput(String textToCheck) throws BadParameterException {
        if (textToCheck == null || textToCheck.length() > 45 || textToCheck.length() < 5) {
            // throw Exception
            throw new BadParameterException("text is null, too long, or too short in CampaignController." + textToCheck);
        }
        textToCheck = textToCheck.replaceAll("\\p{Cc}", "");
        textToCheck = textToCheck.replaceAll(" ", "");
        textToCheck = textToCheck.toLowerCase();
        return textToCheck;
    }


}
