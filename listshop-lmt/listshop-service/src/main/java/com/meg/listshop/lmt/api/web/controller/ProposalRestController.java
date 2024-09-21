/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.auth.api.controller.ProposalRestControllerApi;
import com.meg.listshop.auth.api.model.ProposalResource;

import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.lmt.api.exception.ProposalProcessingException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.data.entity.ProposalEntity;
import com.meg.listshop.lmt.service.proposal.ProposalGeneratorService;
import com.meg.listshop.lmt.service.proposal.ProposalService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class ProposalRestController implements ProposalRestControllerApi {

    private final ProposalService targetProposalService;

    private final ProposalGeneratorService targetProposalGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(ProposalRestController.class);

    @Autowired
    public ProposalRestController(ProposalService targetProposalService, ProposalGeneratorService targetProposalGenerator) {
        this.targetProposalService = targetProposalService;
        this.targetProposalGenerator = targetProposalGenerator;
    }

    @Override
    public ResponseEntity<Object> generateProposal(HttpServletRequest request, Authentication authentication, @PathVariable Long targetId) throws ProposalProcessingException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering generateProposal method for user [{}]", userDetails.getId());
        ProposalEntity proposalEntity = this.targetProposalGenerator.generateProposal(userDetails.getUsername(), targetId);
        if (proposalEntity != null) {
            ProposalResource resource = new ProposalResource(ModelMapper.toModel(proposalEntity));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<ProposalResource> getProposal(Authentication authentication, @PathVariable("proposalId") Long proposalId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering getProposal method for user [{}]", userDetails.getId());
        ProposalEntity proposalEntity = this.targetProposalService.getProposalById(userDetails.getUsername(), proposalId);
        if (proposalEntity != null) {

            // fill tag and dish info for proposal
            proposalEntity = this.targetProposalGenerator.fillInformationForProposal(userDetails.getUsername(), proposalEntity);
            ProposalResource proposalResource = new ProposalResource(ModelMapper.toModel(proposalEntity));
            return new ResponseEntity<>(proposalResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    @Override
    public ResponseEntity<Object> refreshProposal(Authentication authentication, @PathVariable("proposalId") Long proposalId,
                                                  @RequestParam(value = "direction", required = false) String direction) throws ProposalProcessingException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering refreshProposal method for user [{}]", userDetails.getId());
        this.targetProposalGenerator.refreshProposal(userDetails.getUsername(), proposalId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> selectDishInSlot(Authentication authentication, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering selectDishInSlot method for user [{}]", userDetails.getId());
        this.targetProposalService.selectDishInSlot(userDetails.getUsername(), proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> clearDishFromSlot(Authentication authentication, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering clearDishFromSlot method for user [{}]", userDetails.getId());
        this.targetProposalService.clearDishFromSlot(authentication, proposalId, slotId, dishId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> refreshProposalSlot(Authentication authentication, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) throws ProposalProcessingException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LOG.debug("Entering refreshProposalSlot method for user [{}]", userDetails.getId());
        this.targetProposalGenerator.addToProposalSlot(userDetails.getUsername(), proposalId, slotId.intValue());

        return ResponseEntity.noContent().build();
    }

}
