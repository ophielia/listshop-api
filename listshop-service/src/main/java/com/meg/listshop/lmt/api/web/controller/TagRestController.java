package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.TagRestControllerApi;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.entity.TagInfoDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
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

        List<TagInfoDTO> infoTags = tagService.getTagInfoList(userName);
        List<TagResource> resourceList = infoTags.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<TagListResource> retrieveTagList(HttpServletRequest request,
                                                           @RequestParam(value = "filter", required = false) String filter,
                                                           @RequestParam(value = "tag_type", required = false) String tagType,
                                                           @RequestParam(value = "extended", required = false) Boolean extended) {
        List<TagType> tagTypeFilter = processTagTypeInput(tagType);
        TagFilterType tagFilterTypeFilter = filter != null ? TagFilterType.valueOf(filter) : TagFilterType.All;
        if (extended == null) {
            extended = false;
        }
        if (extended || tagFilterTypeFilter == TagFilterType.ParentTags) {
            return retrieveTagExtendedList(tagFilterTypeFilter, tagTypeFilter);
        }

        List<TagEntity> tagList = tagService.getTagList(tagFilterTypeFilter, tagTypeFilter);

        List<TagResource> resourceList = tagList.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity(returnValue, HttpStatus.OK);
    }

    private ResponseEntity<TagListResource> retrieveTagExtendedList(TagFilterType tagFilterTypeFilter, List<TagType> tagTypeFilter) {
        List<TagExtendedEntity> tagList = tagService.getTagExtendedList(tagFilterTypeFilter, tagTypeFilter);

        List<TagResource> resourceList = tagList.stream()
                .map(ModelMapper::toModel)
                .map(TagResource::new)
                .collect(Collectors.toList());
        var returnValue = new TagListResource(resourceList);
        return new ResponseEntity(returnValue, HttpStatus.OK);
    }


    public ResponseEntity<Tag> add(Principal principal, HttpServletRequest request,
                                   @RequestBody Tag input,
                                   @RequestParam(value = "asStandard", required = false, defaultValue = "false") Boolean asStandard) {
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
                                          @RequestParam(value = "asStandard", required = false, defaultValue = "false") Boolean asStandard) {
        String username = null;
        if (!asStandard) {
            username = principal.getName();
        }
        TagEntity parent = this.tagService.getTagById(tagId);

        if (parent != null) {
            var tagEntity = ModelMapper.toEntity(input);
            TagEntity result = this.tagService.createTag(parent, tagEntity, username);
            if (result != null) {
                var tagModel = ModelMapper.toModel(tagEntity);
                var resource = new TagResource(tagModel);
                return ResponseEntity.created(resource.selfLink(request, resource)).build();
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.badRequest().build();

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

    private List<TagType> processTagTypeInput(String tag_type) {
        if (tag_type == null) {
            return new ArrayList<>();
        } else if (tag_type.contains(",")) {
            return Arrays.asList(tag_type.split(",")).stream()
                    .map(t -> TagType.valueOf(t.trim()))
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(TagType.valueOf(tag_type));
        }
    }

}
