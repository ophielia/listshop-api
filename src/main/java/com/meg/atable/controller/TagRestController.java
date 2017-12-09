package com.meg.atable.controller;

import com.meg.atable.api.controller.TagRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.*;
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


    public ResponseEntity<TagResource> retrieveTagList(String filter, String tag_type) {
        List<TagType> tagTypeFilter = processTagTypeInput(tag_type);
        TagFilterType tagFilterTypeFilter = filter != null ? TagFilterType.valueOf(filter) : null;
        List<TagResource> tagList = tagService.getTagList(tagFilterTypeFilter, tagTypeFilter)
                .stream().map(TagResource::new)
                .collect(Collectors.toList());

        Resources<TagResource> tagResourceList = new Resources<>(tagList);
        return new ResponseEntity(tagResourceList, HttpStatus.OK);
    }



    public ResponseEntity<TagResource> add(@RequestBody Tag input) {
        TagEntity tagEntity = ModelMapper.toEntity(input);
        TagEntity result = this.tagService.createTag(null, tagEntity);

        if (result != null) {
            Link forOneTag = new TagResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTag.getHref())).build();

        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<TagResource> addAsChild(@PathVariable Long tagId, @RequestBody Tag input) {
        Optional<TagEntity> parent = this.tagService.getTagById(tagId);

        if (parent.isPresent()) {
            TagEntity tagEntity = ModelMapper.toEntity(input);
            TagEntity result = this.tagService.createTag(parent.get(), tagEntity);
            if (result != null) {
                Link forOneTag = new TagResource(result).getLink("self");
                return ResponseEntity.created(URI.create(forOneTag.getHref())).build();

            } else {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.badRequest().build();

    }

    public ResponseEntity<TagResource> addChildren(@PathVariable Long tagId, @RequestParam(value = "tagIds") String filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

            List<Long> tagIdList = commaDelimitedToList(filter);
        this.tagService.assignChildrenToParent(tagId, tagIdList);
        return ResponseEntity.noContent().build();

    }


    @Override
    public ResponseEntity assignChildToParent(@PathVariable Long parentId, @PathVariable Long childId) {
        if (this.tagService.assignTagToParent(childId, parentId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

@Override
    public ResponseEntity assignChildToBaseTag( @PathVariable("childId") Long childId) {
        if (this.tagService.assignTagToTopLevel(childId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Tag> readTag(@PathVariable Long tagId) {
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
        // invalid tagId - returns invalid id supplied - 400

        // invalid contents of input - returns 405 validation exception

        return this.tagService
                .getTagById(tagId)
                .map(tag -> {
                    tag.setDescription(input.getDescription());
                    tag.setName(input.getName());
                    tag.setTagType(TagType.valueOf(input.getTagType()));
                    tagService.save(tag);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }


    private List<TagType> processTagTypeInput(String tag_type) {
        if (tag_type == null) {
            return null;
        } else if (tag_type.contains(",")) {
            return Arrays.asList(tag_type.split(",")).stream()
                    .map(t -> TagType.valueOf(t.trim()))
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(TagType.valueOf(tag_type));
        }
    }

    private List<Long> commaDelimitedToList(String commaSeparatedIds) {
// translate tags into list of Long ids
        if (commaSeparatedIds == null) {
            return new ArrayList<>();
        }
        String[] ids = commaSeparatedIds.split(",");
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());

    }
}
