package com.meg.atable.controller;

import com.meg.atable.api.controller.TagRestControllerApi;
import com.meg.atable.api.model.Tag;
import com.meg.atable.api.model.TagResource;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class TagRestController implements TagRestControllerApi {

    private final TagService tagService;

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    public ResponseEntity<TagResource> retrieveTagList() {
        List<TagResource> tagList = tagService.getTagList()
                .stream().map(TagResource::new)
                .collect(Collectors.toList());

        Resources<TagResource> tagResourceList = new Resources<>(tagList);
        return new ResponseEntity(tagResourceList, HttpStatus.OK);
    }

    public ResponseEntity<TagResource> add(@RequestBody Tag input) {
        TagEntity result = this.tagService.createTag(null, input.getName(), input.getDescription());

        if (result != null) {
            Link forOneTag = new TagResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTag.getHref())).build();

        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<TagResource> addAsChild(@PathVariable Long tagId, @RequestBody Tag input) {
        Optional<TagEntity> parent = this.tagService.getTagById(tagId);

        TagEntity result = this.tagService.createTag(parent.get(), input.getName(), input.getDescription());

        if (result != null) {
            Link forOneTag = new TagResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTag.getHref())).build();

        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<Tag> readTag(@PathVariable Long tagId) {
        // MM
        // invalid dishId - returns invalid id supplied - 400

        return this.tagService
                .getTagById(tagId)
                .map(tag -> {
                    TagResource tagResource = new TagResource(tag);

                    return new ResponseEntity(tagResource, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());

    }

    public ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input) {
        // MM
        // invalid tagId - returns invalid id supplied - 400

        // MM
        // invalid contents of input - returns 405 validation exception

        return this.tagService
                .getTagById(tagId)
                .map(tag -> {
                    tag.setDescription(input.getDescription());
                    tag.setName(input.getName());

                    tagService.save(tag);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }



}
