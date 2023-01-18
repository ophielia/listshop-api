package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.exception.BadParameterException;
import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/tag")
public interface TagRestControllerApi {


    @GetMapping(value = "/user")
    ResponseEntity<TagListResource> retrieveUserTagList(
            Principal principal,
            HttpServletRequest request);

    // remove this in August 2022
    @Deprecated(forRemoval = true)
    @GetMapping
    ResponseEntity<TagListResource> retrieveTagList(Principal principal, HttpServletRequest request,
                                                    @RequestParam(value = "extended", required = false) Boolean extended);

    @Deprecated(forRemoval = true)
    @PostMapping(produces = "application/json", consumes = "application/json")
    ResponseEntity<Tag> add(Principal principal, HttpServletRequest request, @RequestBody Tag input,
                            @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException;

    @PostMapping(value = "{tagId}/child",  produces = "application/json", consumes = "application/json")
    ResponseEntity<Tag> addAsChild(Principal principal, HttpServletRequest request, @PathVariable Long tagId, @RequestBody Tag input,
                                   @RequestParam(value = "asStandard", required = false, defaultValue = "false") boolean asStandard) throws BadParameterException;


    @GetMapping( value = "/{tagId}", produces = "application/json")
    ResponseEntity<Tag> readTag(HttpServletRequest request, @PathVariable("tagId") Long tagId);



}
