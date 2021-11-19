package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.Target;
import com.meg.listshop.lmt.api.model.TargetResource;
import com.meg.listshop.lmt.api.model.TargetSlot;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/target")
@CrossOrigin
public interface TargetRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<Resources<TargetResource>> retrieveTargets(Principal principal);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createTarget(Principal principal, @RequestBody Target input);

    @RequestMapping(method = RequestMethod.POST,value = "/pickup", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createPickupTarget(Principal principal, @RequestBody Target input,
                                              @RequestParam(value = "pickupTags", required = false) String pickupTags);

    @RequestMapping(method = RequestMethod.GET, value = "/{targetId}", produces = "application/json")
    ResponseEntity<Target> readTarget(Principal principal, @PathVariable("targetId") Long targetId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{targetId}", produces = "application/json")
    ResponseEntity<Target> deleteTarget(Principal principal, @PathVariable("targetId") Long targetId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{targetId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateTarget(Principal principal, @PathVariable Long targetId, @RequestBody Target input);

    @RequestMapping(method = RequestMethod.POST, value = "/{targetId}/slot", produces = "application/json")
    ResponseEntity<Object> addSlotToTarget(Principal principal, @PathVariable Long targetId, @RequestBody TargetSlot input);


    @RequestMapping(method = RequestMethod.DELETE, value = "/{targetId}/slot/{slotId}", produces = "application/json")
    ResponseEntity<Object> deleteSlotFromTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId);

    @RequestMapping(method = RequestMethod.POST, value = "/{targetId}/slot/{slotId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addTagToSlot(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId, @PathVariable Long tagId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{targetId}/slot/{slotId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> deleteTagFromSlot(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId, @PathVariable Long tagId);


    @RequestMapping(method = RequestMethod.POST, value = "/{targetId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addTagToTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long tagId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{targetId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> deleteTagFromTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long tagId);

}
