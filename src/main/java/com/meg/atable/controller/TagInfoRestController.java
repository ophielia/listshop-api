package com.meg.atable.controller;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagInfoResource;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

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


    public ResponseEntity<TagInfoResource> retrieveTagList(@RequestParam(value = "tag_type", required = false) String tag_type) {
        TagType tagType = tag_type == null ? null : TagType.valueOf(tag_type);

        // get tag list
        List<TagEntity> tagList = (List) tagService.getTagList(TagFilterType.All,tagType);

        // fill in relationship info
        tagList = tagService.fillInRelationshipInfo(tagList);

        // create taginforesource
        TagInfoResource tagInfo = new TagInfoResource(tagList);

        return new ResponseEntity(tagInfo, HttpStatus.OK);
    }

}
