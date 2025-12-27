package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.lmt.api.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/admin/layout/category")
public interface AdminLayoutRestControllerApi {



    @GetMapping()
    ResponseEntity<LayoutCategoryListResource> getStandardLayoutCategories();

    @PutMapping(value = "/{categoryId}/tag/{tagId}")
    ResponseEntity<Object> assignStandardLayoutCategoryToTag(@PathVariable("tagId") Long tagId, @PathVariable("categoryId") Long categoryId);

}
