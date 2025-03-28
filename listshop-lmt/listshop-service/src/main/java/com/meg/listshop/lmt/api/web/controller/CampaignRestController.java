package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.common.StringTools;
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

import jakarta.servlet.http.HttpServletRequest;
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
        // check input params
        String email = checkTextInput(input.getEmail(), 7, 45, true, true, false);
        String campaign = checkTextInput(input.getCampaign(), 4, 30, true, true, true);
        String feedback = checkTextInput(input.getText(), 1, 500, false, false, true);

        // send off to the service
        campaignService.addCampaignEmail(campaign, email, feedback);
        return ResponseEntity.noContent().build();
    }

    private String checkTextInput(String textToCheck, Integer minLength, Integer maxLength, boolean toLower, boolean stripSpaces, boolean isRequired) throws BadParameterException {
        var checked = StringTools.safetyCheckTextInput(textToCheck, maxLength, minLength, true, true);
        if (isRequired && checked == null) {
            throw new BadParameterException("text is null, too long, or too short in CampaignController." + textToCheck);
        }
        return textToCheck;
    }


}
