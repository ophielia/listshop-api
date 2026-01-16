/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.controller;

import com.meg.listshop.auth.api.model.ProposalResource;
import com.meg.listshop.lmt.api.exception.ProposalProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/proposal")
public interface ProposalRestControllerApi {

    @RequestMapping(method = RequestMethod.POST, value = "/target/{targetId}", produces = "application/json")
    ResponseEntity<Object> generateProposal(HttpServletRequest request, Authentication authentication, @PathVariable("targetId") Long targetId) throws ProposalProcessingException;

    @RequestMapping(method = RequestMethod.GET, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<ProposalResource> getProposal(Authentication authentication, @PathVariable("proposalId") Long proposalId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<Object> refreshProposal(Authentication authentication, @PathVariable("proposalId") Long proposalId,
                                           @RequestParam(value = "direction", required = false) String direction) throws ProposalProcessingException;

    @RequestMapping(method = RequestMethod.POST, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> selectDishInSlot(Authentication authentication, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId, @PathVariable("dishId") Long dishId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> clearDishFromSlot(Authentication authentication, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId, @PathVariable("dishId") Long dishId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}/slot/{slotId}", produces = "application/json")
    ResponseEntity<Object> refreshProposalSlot(Authentication authentication, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) throws ProposalProcessingException;


}
