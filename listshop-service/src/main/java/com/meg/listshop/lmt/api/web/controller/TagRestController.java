package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.TagRestControllerApi;
import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.TagResource;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.service.tag.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class TagRestController implements TagRestControllerApi {

    private final TagService tagService;

    private static final Logger logger = LogManager.getLogger(TagRestController.class);

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    public ResponseEntity<TagListResource> retrieveUserTagList(
            Principal principal,
            HttpServletRequest request) {
        String userName = principal != null ? principal.getName() : null;

        List<TagInfoDTO> infoTags = tagService.getTagInfoList(userName, Collections.emptyList());
        List<TagResource> resourceList = infoTags.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<TagListResource> retrieveTagList(Principal principal, HttpServletRequest request,
                                                           @RequestParam(value = "extended", required = false) Boolean extended) {
        return retrieveUserTagList(principal, request);
        // when we remove this, also remove
        // * search_select and assign_select from db, model, mapper
        // * remove view tag_extended, plus associated entity and repository

    }


    public ResponseEntity<Tag> add(Principal principal, HttpServletRequest request,
                                   @RequestBody Tag input,
                                   @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException {
        var tagEntity = ModelMapper.toEntity(input);
        String userName = null;
        if (!asStandard) {
            userName = principal.getName();
        }
        TagEntity result = this.tagService.createTag(null, tagEntity, userName);

        if (result != null) {

            var tagModel = ModelMapper.toModel(tagEntity);
            var resource = new TagResource(tagModel);
            return ResponseEntity.created(resource.selfLink(request, resource)).build();

        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Tag> addAsChild(Principal principal, HttpServletRequest request, @PathVariable Long tagId, @RequestBody Tag input,
                                          @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException {
        logger.debug("Beginning add tag.");
        String username = null;
        if (!asStandard) {
            username = principal.getName();
        }

        var tagEntity = ModelMapper.toEntity(input);
        TagEntity result = this.tagService.createTag(tagId, tagEntity, username);
        if (result != null) {
            var tagModel = ModelMapper.toModel(result);
            var resource = new TagResource(tagModel);
            return ResponseEntity.created(resource.selfLink(request, resource)).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    public ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable Long tagId) {
        // invalid dishId - returns invalid id supplied - 400
        var tagEntity = this.tagService
                .getTagById(tagId);

        if (tagEntity == null) {
            return ResponseEntity.notFound().build();
        }
        var tagModel = ModelMapper.toModel(tagEntity);

        return new ResponseEntity(new TagResource(tagModel), HttpStatus.OK);

    }

}
