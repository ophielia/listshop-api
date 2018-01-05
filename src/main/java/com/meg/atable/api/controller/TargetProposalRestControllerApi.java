package com.meg.atable.api.controller;

import com.meg.atable.api.model.Target;
import com.meg.atable.api.model.TargetProposalResource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/proposal")
public interface TargetProposalRestControllerApi {

    @RequestMapping(method = RequestMethod.POST, value = "/target/{targetId}", produces = "application/json")
    ResponseEntity<Object> generateProposal(Principal principal, @PathVariable Long targetId);

    @RequestMapping(method = RequestMethod.GET, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<TargetProposalResource> getProposal(Principal principal, @PathVariable("proposalId") Long proposalId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}", produces = "application/json")
    ResponseEntity<Object> refreshProposal(Principal principal, @PathVariable("proposalId") Long proposalId);

    @RequestMapping(method = RequestMethod.POST, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> selectDishInSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{proposalId}/slot/{slotId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> clearDishFromSlot(Principal principal, @PathVariable Long proposalId, @PathVariable Long slotId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{proposalId}/slot/{slotId}", produces = "application/json")
    ResponseEntity<Object> refreshProposalSlot(Principal principal, @PathVariable("proposalId") Long proposalId, @PathVariable("slotId") Long slotId);




}
