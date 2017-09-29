package com.meg.atable.controller;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.model.TagInfo;
import com.meg.atable.api.model.TagInfoResource;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
        // get tag list
        List<TagEntity> entities = (List)tagService.getTagList(); //MM todo - fix method signatures to avoid cast

        // fill in relationship info
        entities = tagService.fillInRelationshipInfo(entities);

        // create taginforesource
        TagInfoResource tagInfo = new TagInfoResource(entities);

        return new ResponseEntity(tagInfo, HttpStatus.OK);
    }

}
