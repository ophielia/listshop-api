package com.meg.listshop.lmt.api.web.controller;

import com.meg.listshop.lmt.api.controller.TagInfoRestControllerApi;
import com.meg.listshop.lmt.api.model.FatTag;
import com.meg.listshop.lmt.api.model.TagDrilldownResource;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Controller
@Deprecated
public class TagInfoRestController implements TagInfoRestControllerApi {

    private final TagStructureService tagStructureService;

    @Autowired
    TagInfoRestController(TagStructureService tagStructureService) {
        this.tagStructureService = tagStructureService;
    }

    public ResponseEntity<List<TagDrilldownResource>> retrieveTagList(@RequestParam(value = "tag_type", required = false) String tag_type) {
        List<TagType> tagTypes = processTagTypeInput(tag_type);
        List<FatTag> filledTags = new ArrayList<>(); //ntagStructureService.getTagsWithChildren(tagTypes);

        // create taginforesource
        List<TagDrilldownResource> resource = filledTags.stream()
                .map(TagDrilldownResource::new)
                .collect(Collectors.toList());

        return new ResponseEntity(resource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TagDrilldownResource>> retrieveTagListRefresh(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after
    ) {
        // List<FatTag> filledTags = tagStructureService.getChangedTagsWithChildren(after);
        List<FatTag> filledTags = new ArrayList<>();

        // create taginforesource
        List<TagDrilldownResource> resource = filledTags.stream()
                .map(TagDrilldownResource::new)
                .collect(Collectors.toList());

        return new ResponseEntity(resource, HttpStatus.OK);
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
