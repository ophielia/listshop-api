package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.TargetRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TargetEntity;
import com.meg.listshop.lmt.data.entity.TargetSlotEntity;
import com.meg.listshop.lmt.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class TargetRestController implements TargetRestControllerApi {

    TargetService targetService;

    @Autowired
    public TargetRestController(TargetService targetService) {
        this.targetService = targetService;
    }

    @Override
    public ResponseEntity<TargetListResource> retrieveTargets(Principal principal) {
        List<TargetResource> targetList = targetService
                .getTargetsForUserName(principal.getName(), false)
                .stream()
                .map(ModelMapper::toModel)
                .map(TargetResource::new)
                .collect(Collectors.toList());

        TargetListResource targetResourceList = new TargetListResource(targetList);
        return new ResponseEntity(targetResourceList, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> createTarget(HttpServletRequest request, Principal principal, @RequestBody Target input) {
        //this.getUserForPrincipal(principal);
        TargetEntity targetEntity = ModelMapper.toEntity(input);

        TargetEntity result = targetService.createTarget(principal.getName(), targetEntity);

        if (result != null) {
            TargetResource resource = new TargetResource(ModelMapper.toModel(result));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    public ResponseEntity<Object> createPickupTarget(HttpServletRequest request, Principal principal, @RequestBody Target input,
                                                     @RequestParam(value = "pickupTags", required = false) String pickupTags) {
        List<Long> tagIdList = commaDelimitedToList(pickupTags);
        TargetEntity targetEntity = ModelMapper.toEntity(input);

        TargetEntity result = targetService.createTarget(principal.getName(), targetEntity);

        TargetSlotEntity resultSlot = targetService.addDefaultTargetSlot(principal.getName(), result);

        if (!tagIdList.isEmpty()) {
            tagIdList.stream().forEach(t -> targetService.addTagToTargetSlot(principal.getName(), result.getTargetId(), resultSlot.getId(), t));
        }

        if (result != null) {
            TargetResource resource = new TargetResource(ModelMapper.toModel(result));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Target> readTarget(Principal principal, @PathVariable Long targetId) {
        TargetEntity target = this.targetService
                .getTargetById(principal.getName(), targetId);

        // fill tag info for target
        target = this.targetService.fillTagsForTarget(target);

        if (target != null) {
            TargetResource targetResource = new TargetResource(ModelMapper.toModel(target));

            return new ResponseEntity(targetResource, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();

    }

    @Override
    public ResponseEntity<Target> deleteTarget(Principal principal, @PathVariable Long targetId) {
        boolean success = targetService.deleteTarget(principal.getName(), targetId);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> updateTarget(HttpServletRequest request, Principal principal, @PathVariable Long targetId, @RequestBody Target input) {
        //this.getUserForPrincipal(principal);
        TargetEntity targetEntity = ModelMapper.toEntity(input);

        TargetEntity result = targetService.updateTarget(principal.getName(), targetEntity);

        if (result != null) {
            TargetResource resource = new TargetResource(ModelMapper.toModel(result));
            String link = resource.selfLink(request, resource).toString();
            return ResponseEntity.created(URI.create(link)).build();
        }
        return ResponseEntity.badRequest().build();

    }

    @Override
    public ResponseEntity<Object> addSlotToTarget(Principal principal, @PathVariable Long targetId, @RequestBody TargetSlot input) {
        TargetSlotEntity targetSlotEntity = ModelMapper.toEntity(input);
        this.targetService.addSlotToTarget(principal.getName(), targetId, targetSlotEntity);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> deleteSlotFromTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId) {
        this.targetService.deleteSlotFromTarget(principal.getName(), targetId, slotId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> addTagToSlot(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId, @PathVariable Long tagId) {
        this.targetService.addTagToTargetSlot(principal.getName(), targetId, slotId, tagId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteTagFromSlot(Principal principal, @PathVariable Long targetId, @PathVariable Long slotId, @PathVariable Long tagId) {
        this.targetService.deleteTagFromTargetSlot(principal.getName(), targetId, slotId, tagId);

        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<Object> addTagToTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long tagId) {
        this.targetService.addTagToTarget(principal.getName(), targetId, tagId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> deleteTagFromTarget(Principal principal, @PathVariable Long targetId, @PathVariable Long tagId) {
        this.targetService.deleteTagFromTarget(principal.getName(), targetId, tagId);

        return ResponseEntity.noContent().build();
    }

    private List<Long> commaDelimitedToList(String commaSeparatedIds) {
// translate tags into list of Long ids
        if (commaSeparatedIds == null) {
            return new ArrayList<>();
        }
        String[] ids = commaSeparatedIds.split(",");
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());

    }
}
