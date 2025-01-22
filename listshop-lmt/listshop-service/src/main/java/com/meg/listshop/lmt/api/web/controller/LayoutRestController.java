package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.controller.LayoutRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.service.LayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class LayoutRestController implements LayoutRestControllerApi {

    private final UserService userService;

    private final LayoutService layoutService;

    private static final Logger LOG = LoggerFactory.getLogger(LayoutRestController.class);

    @Autowired
    public LayoutRestController(UserService userService, LayoutService layoutService) {
        this.userService = userService;
        this.layoutService = layoutService;
    }

    @Override
    public ResponseEntity<Object> addUserLayoutMapping(HttpServletRequest httpServletRequest, Principal principal, MappingPost mappingPost) {
        // gather info from input and validate
        UserEntity user = userService.getUserByUserEmail(principal.getName());
        Long categoryId = StringTools.stringToLong(mappingPost.getCategoryId());
        List<Long> tagIds = StringTools.stringListToLongs(mappingPost.getTagIds());

        if (categoryId == null || tagIds.isEmpty()) {
            // return bad request
            return ResponseEntity.badRequest().build();
        }

        // service call
        try {
            layoutService.addDefaultUserMappings(user.getId(), categoryId, tagIds);
        } catch (ObjectNotFoundException e) {
            LOG.error("Exception while inserting mapping: ", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ListLayoutListResource> retrieveUserLayouts(HttpServletRequest request, Principal principal) {
        UserEntity user = userService.getUserByUserEmail(principal.getName());

        // service call
        List<ListLayoutResource> listOfLayoutsResource;
        try {
            listOfLayoutsResource = layoutService.getUserLayouts(user)
                    .stream()
                    .map(ModelMapper::toShortModel)
                    .map(ListLayoutResource::new)
                    .collect(Collectors.toList());
        } catch (ObjectNotFoundException e) {
            LOG.error("Exception while retrieving user layouts ", e);
            return ResponseEntity.badRequest().build();
        }

        ListLayoutListResource resource = new ListLayoutListResource(listOfLayoutsResource);
        resource.fillLinks(request, resource);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CategoryListResource> retrieveUserCategories(HttpServletRequest request, Principal principal) {

        // service call
        List<CategoryResource> listOfCategoryResources;
        try {
            listOfCategoryResources = layoutService.getUserCategories(principal.getName())
                    .stream()
                    .map(ModelMapper::toModel)
                    .map(CategoryResource::new)
                    .collect(Collectors.toList());
        } catch (ObjectNotFoundException e) {
            LOG.error("Exception while retrieving user layouts ", e);
            return ResponseEntity.badRequest().build();
        }

        CategoryListResource resource = new CategoryListResource(listOfCategoryResources);
        resource.fillLinks(request, resource);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListLayoutResource> retrieveDefaultLayout(HttpServletRequest request, Principal principal) {
        // service call
        Long userId = null;
        if (principal != null) {
            UserEntity user = userService.getUserByUserEmail(principal.getName());
            userId = user.getId();
        }
        try {
            ListLayout layout = ModelMapper.toShortModel(layoutService.getFilledStandardLayout(userId));
            ListLayoutResource layoutResource = new ListLayoutResource(layout);
            layoutResource.fillLinks(request, layoutResource);
            return new ResponseEntity<>(layoutResource, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            LOG.error("Exception while retrieving user layouts ", e);
            return ResponseEntity.badRequest().build();
        }
    }




}

