package com.meg.listshop.lmt.api.web.controller;


import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.lmt.api.controller.TagRestControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.service.tag.TagService;
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

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class TagRestController implements TagRestControllerApi {

    private final TagService tagService;

    private static final Logger logger = LoggerFactory.getLogger(TagRestController.class);

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    public ResponseEntity<TagListResource> retrieveUserTagList(
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = null;
        if (authentication == null) {
            String message = String.format("Retrieving tags for anonymous user");
            logger.info(message);
        } else {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = userDetails.getId();
            String message = String.format("Retrieving tags for user [%S]", userId);
            logger.info(message);
        }

        List<TagInfoDTO> infoTags = tagService.getTagInfoList(userId, Collections.emptyList());
        List<TagResource> resourceList = infoTags.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<Tag> addAsChild(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId, @RequestBody Tag input,
                                          @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String message = String.format("Creating add tag for user [%S]", userDetails.getId());
        logger.info(message);

        Long userId = null;
        if (!asStandard) {
            userId = userDetails.getId();
        }

        var tagEntity = ModelMapper.toEntity(input);
        TagEntity result = this.tagService.createTag(tagId, tagEntity, userId);
        if (result != null) {
            var tagModel = ModelMapper.toModel(result);
            var resource = new TagResource(tagModel);
            return ResponseEntity.created(resource.selfLink(request, resource)).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    public ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable("tagId") Long tagId) {
        // invalid dishId - returns invalid id supplied - 400
        var tagEntity = this.tagService
                .getTagById(tagId);

        if (tagEntity == null) {
            return ResponseEntity.notFound().build();
        }
        var tagModel = ModelMapper.toModel(tagEntity);

        return new ResponseEntity(new TagResource(tagModel), HttpStatus.OK);

    }

    @PutMapping(value = "{tagId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> updateTag(Authentication authentication, HttpServletRequest request, @PathVariable("tagId") Long tagId, @RequestBody TagPut input) throws BadParameterException {
        String tagName = input.getName();
        tagService.updateTagName(tagId, tagName);
        return ResponseEntity.noContent().build();
    }

}
