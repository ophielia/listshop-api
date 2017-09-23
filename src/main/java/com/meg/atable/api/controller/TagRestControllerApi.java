package com.meg.atable.api.controller;

import com.meg.atable.api.model.Tag;
import com.meg.atable.api.model.TagResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/tag")
public interface TagRestControllerApi {

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<TagResource> retrieveTagList();

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<TagResource> add(@RequestBody Tag input);

    @RequestMapping(value = "{tagId}/child", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<TagResource> addAsChild(@PathVariable Long tagId, @RequestBody Tag input);

    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}", produces = "application/json")
    ResponseEntity<Tag> readTag(@PathVariable("tagId") Long tagId);

    @RequestMapping(method = RequestMethod.PUT, value = "/{tagId}", consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable Long tagId, @RequestBody Tag input);


}
