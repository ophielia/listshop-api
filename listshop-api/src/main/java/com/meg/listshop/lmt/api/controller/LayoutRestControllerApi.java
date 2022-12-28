package com.meg.listshop.lmt.api.controller;


import com.meg.listshop.lmt.api.model.CategoryListResource;
import com.meg.listshop.lmt.api.model.ListLayoutListResource;
import com.meg.listshop.lmt.api.model.ListLayoutResource;
import com.meg.listshop.lmt.api.model.MappingPost;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/layout")
@CrossOrigin
public interface LayoutRestControllerApi {

    @PostMapping(value = "/user/mapping", produces = "application/json")
    ResponseEntity<Object> addUserLayoutMapping(HttpServletRequest request, Principal principal, @RequestBody MappingPost input);

    @GetMapping(value = "/user", produces = "application/json")
    ResponseEntity<ListLayoutListResource> retrieveUserLayouts( HttpServletRequest request, Principal principal);

    @GetMapping(value = "/user/categories", produces = "application/json")
    ResponseEntity<CategoryListResource> retrieveUserCategories(HttpServletRequest request, Principal principal);

    @GetMapping(value = "/default", produces = "application/json")
    ResponseEntity<ListLayoutResource> retrieveDefaultLayout(HttpServletRequest request, Principal principal);

}
