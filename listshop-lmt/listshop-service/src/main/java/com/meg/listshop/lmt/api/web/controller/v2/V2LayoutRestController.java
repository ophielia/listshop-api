/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.web.controller.v2;

import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.controller.v2.V2LayoutRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.MappingPost;
import com.meg.listshop.lmt.api.model.v2.ListLayout;
import com.meg.listshop.lmt.api.model.v2.ListLayoutCategory;
import com.meg.listshop.lmt.api.model.v2.ListLayoutList;
import com.meg.listshop.lmt.api.model.v2.V2ModelMapper;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.service.LayoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class V2LayoutRestController implements V2LayoutRestControllerApi {

    private static final Logger LOG = LoggerFactory.getLogger(V2LayoutRestController.class);
    private final UserService userService;
    private final LayoutService layoutService;

    @Autowired
    public V2LayoutRestController(UserService userService,
                                  @Qualifier("V2LayoutService") LayoutService layoutService) {
        this.userService = userService;
        this.layoutService = layoutService;
    }

    @Override
    public ResponseEntity<Object> addUserLayoutMapping(HttpServletRequest httpServletRequest, Authentication authentication, MappingPost mappingPost) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        Long userId = userDetails != null ? userDetails.getId() : null;

        Long categoryId = StringTools.stringToLong(mappingPost.getCategoryId());
        List<Long> tagIds = StringTools.stringListToLongs(mappingPost.getTagIds());

        if (categoryId == null || tagIds.isEmpty()) {
            // return bad request
            return ResponseEntity.badRequest().build();
        }

        // service call
        try {
            layoutService.addDefaultUserMappings(userId, categoryId, tagIds);
        } catch (ObjectNotFoundException e) {
            LOG.error("Exception while inserting mapping: ", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ListLayoutList> retrieveAllLayouts(HttpServletRequest request, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        Long userId = userDetails != null ? userDetails.getId() : null;

        // service call
        List<ListLayout> listLayouts;
        listLayouts = layoutService.getAllLayouts(userId)
                .stream()
                .map(V2ModelMapper::toModel)
                .collect(Collectors.toList());


        ListLayoutList listLayoutList = new ListLayoutList(listLayouts);
        return new ResponseEntity<>(listLayoutList, HttpStatus.OK);
    }

    private CustomUserDetails getUserDetails(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @Override
    public ResponseEntity<ListLayoutCategory> getCategoryForTag(HttpServletRequest request, @PathVariable("tagId") Long tagId, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        Long userId = userDetails != null ? userDetails.getId() : null;

        // service call
        ListLayoutCategoryEntity categoryEntity = layoutService.getCategoryForTag(userId, tagId);
        ListLayoutCategory category = V2ModelMapper.toModel(categoryEntity);

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

}

