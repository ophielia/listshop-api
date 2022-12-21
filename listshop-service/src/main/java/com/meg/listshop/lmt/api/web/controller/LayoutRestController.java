package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.controller.LayoutRestControllerApi;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.ListLayoutListResource;
import com.meg.listshop.lmt.api.model.ListLayoutResource;
import com.meg.listshop.lmt.api.model.MappingPost;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.service.LayoutService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Controller
public class LayoutRestController implements LayoutRestControllerApi {

    private UserService userService;

    private LayoutService layoutService;

    private static final Logger LOG = LogManager.getLogger(LayoutRestController.class);

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
        List<ListLayoutResource> listOfLayoutsResource = new ArrayList<>();
        try {
            listOfLayoutsResource = layoutService.getUserLayouts(user)
                    .stream()
                    .map(ModelMapper::toModel)
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
}
