/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.controller.v2;


import com.meg.listshop.lmt.api.model.MappingPost;
import com.meg.listshop.lmt.api.model.v2.ListLayoutCategory;
import com.meg.listshop.lmt.api.model.v2.ListLayoutList;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/v2/layout")
@CrossOrigin
public interface V2LayoutRestControllerApi {

    @PostMapping(value = "/user/mapping", produces = "application/json")
    ResponseEntity<Object> addUserLayoutMapping(HttpServletRequest request, Authentication authentication, @RequestBody MappingPost input);

    @GetMapping(produces = "application/json")
    ResponseEntity<ListLayoutList> retrieveAllLayouts(HttpServletRequest request, Authentication authentication);

    @GetMapping(value = "/tag/{tagId}" ,produces = "application/json")
    ResponseEntity<ListLayoutCategory> getCategoryForTag(HttpServletRequest request, @RequestParam Long tagId, Authentication authentication);

}
