package com.meg.atable.api;

import com.meg.atable.model.Tag;
import com.meg.atable.model.TagInfo;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/taginfo")
public class TagInfoRestController {

    private final TagService tagService;

    @Autowired
    TagInfoRestController(TagService tagService) {
        this.tagService = tagService;
    }


    @RequestMapping(method = RequestMethod.GET,produces = "application/json")
    ResponseEntity<TagInfoResource> retrieveTagList(@RequestParam(value="filter",required=false) String filter) {
        // ignoring filter for now
        boolean rootOnly=false;
        List<TagInfoResource> tagList = tagService.getTagInfoList( rootOnly)
                .stream().map(TagInfoResource::new)
                .collect(Collectors.toList());

        Resources<TagInfoResource> tagResourceList = new Resources<>(tagList);
        return new ResponseEntity(tagResourceList, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}", produces = "application/json")
    ResponseEntity<TagInfoResource> readTag(@PathVariable Long tagId) {
        // MM
        // invalid dishId - returns invalid id supplied - 400
        TagInfo tagInfo = tagService.getTagInfo(tagId);
        if (tagInfo != null) {
            TagInfoResource tagInfoResource = new TagInfoResource(tagInfo);
            return new ResponseEntity(tagInfoResource,HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{parentId}/child/{tagId}")
    ResponseEntity<Object> addTagAsChild(@PathVariable Long tagId, @PathVariable Long parentId) {
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
