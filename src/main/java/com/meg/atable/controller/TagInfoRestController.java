package com.meg.atable.controller;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.model.TagInfo;
import com.meg.atable.api.model.TagInfoResource;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Controller
public class TagInfoRestController implements TagInfoRestControllerApi {

    private final TagService tagService;

    @Autowired
    TagInfoRestController(TagService tagService) {
        this.tagService = tagService;
    }


    public ResponseEntity<TagInfoResource> retrieveTagList(@RequestParam(value="filter",required=false) String filter) {
        // ignoring filter for now
        boolean rootOnly=false;
        List<TagInfoResource> tagList = tagService.getTagInfoList( rootOnly)
                .stream().map(TagInfoResource::new)
                .collect(Collectors.toList());

        Resources<TagInfoResource> tagResourceList = new Resources<>(tagList);
        return new ResponseEntity(tagResourceList, HttpStatus.OK);
    }


    public ResponseEntity<TagInfoResource> readTag(@PathVariable Long tagId) {
        // MM
        // invalid dishId - returns invalid id supplied - 400
        TagInfo tagInfo = tagService.getTagInfo(tagId);
        if (tagInfo != null) {
            TagInfoResource tagInfoResource = new TagInfoResource(tagInfo);
            return new ResponseEntity(tagInfoResource,HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Object> addTagAsChild(@PathVariable Long tagId, @PathVariable Long parentId) {
        // MM
        // invalid tagId - returns invalid id supplied - 400

        // MM
        // invalid contents of input - returns 405 validation exception

        boolean success = this.tagService.assignTagToParent( tagId,  parentId);

        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
