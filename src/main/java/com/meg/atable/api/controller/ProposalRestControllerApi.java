package com.meg.atable.api.controller;

import com.meg.atable.api.exception.ProposalProcessingException;
import com.meg.atable.api.model.ProposalResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/proposal")
public interface ProposalRestControllerApi {

    @RequestMapping(method = RequestMethod.POST, value = "/target/{targetId}", produces = "application/json")
    ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId) throws ProposalProcessingException;

    @RequestMapping(method = RequestMethod.GET, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<ProposalResource> getProposal(Principal principal, @PathVariable("proposalId") Long proposalId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<Object> refreshProposal(Principal principal, @PathVariable("proposalId") Long proposalId,
                                           @RequestParam(value = "direction", required = false) String direction) throws ProposalProcessingException;

    @RequestMapping(method = RequestMethod.POST, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> selectDishInSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> clearDishFromSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}/slot/{slotId}", produces = "application/json")
    ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId) throws ProposalProcessingException;


}
