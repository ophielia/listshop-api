package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.TagListResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amount")
public interface AmountRestControllerApi {


    // returns a list of markers and unit sizes
    @GetMapping(value = "/modifiers")
    ResponseEntity<TagListResource> retriedveUserTagList();

    // returns a list of units for given tag
    @GetMapping(value = "/{tagId}/unit")
    ResponseEntity<TagListResource> retrieveUserTagList();

    // returns a list of units for domain and liquid / not liquid (optional)
    @GetMapping(value = "/unit")
    ResponseEntity<TagListResource> retrievennUserTagList();

}
