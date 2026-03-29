/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.web.controller.v2;


import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.common.ControllerUtils;
import com.meg.listshop.lmt.api.controller.v2.V2TagRestControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.v2.V2ModelMapper;
import com.meg.listshop.lmt.api.model.TagPut;
import com.meg.listshop.lmt.api.model.v2.Tag;
import com.meg.listshop.lmt.api.model.v2.TagList;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.service.tag.TagService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller(value="V2TagRestController")
public class TagRestController implements V2TagRestControllerApi {

    private final TagService tagService;

    private static final Logger logger = LoggerFactory.getLogger(TagRestController.class);

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    public ResponseEntity<TagList> retrieveUserTagList(
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = null;
        if (authentication == null) {
            String message = "V2 Retrieving tags for anonymous user";
            logger.info(message);
        } else {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = userDetails.getId();
            String message = String.format("V2 Retrieving tags for user [%S]", userId);
            logger.info(message);
        }

        List<TagInfoDTO> infoTags = tagService.getTagInfoList(userId, Collections.emptyList());
        List<Tag> tagList = infoTags.stream()
                .map(V2ModelMapper::toModel)
                .toList();
        var returnValue = new TagList(tagList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<Object> addAsChild(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId, @RequestBody Tag input,
                                             @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException, MalformedURLException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String message = String.format("Creating add tag for user [%S]", userDetails.getId());
        logger.info(message);

        Long userId = null;
        if (!asStandard) {
            userId = userDetails.getId();
        }

        var tagEntity = V2ModelMapper.toEntity(input);
        TagEntity result = this.tagService.createTag(tagId, tagEntity, userId);
        if (result != null) {
            var location = ControllerUtils.locationURI(request, "/v2/tags", result.getId());
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    public ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable("tagId") Long tagId,Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        Long userId = userDetails != null ? userDetails.getId() : null;
        // invalid dishId - returns invalid id supplied - 400
        var tagInfo = this.tagService
                .getTagInfoList(userId,tagId);

        if (tagInfo == null ) {
            return ResponseEntity.notFound().build();
        }
        var tagModel = V2ModelMapper.toModel(tagInfo);

        return new ResponseEntity<>(tagModel, HttpStatus.OK);

    }

    private CustomUserDetails getUserDetails(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @PutMapping(value = "{tagId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> updateTag(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId, @RequestBody TagPut input) throws BadParameterException {
        String tagName = input.getName();
        tagService.updateTagName(tagId, tagName);
        return ResponseEntity.noContent().build();
    }

}
