/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.admin.model.PostSearchTags;
import com.meg.listshop.admin.model.PostUpdateTags;
import com.meg.listshop.lmt.api.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/admin/food")
public interface AdminFoodRestControllerApi {


    @GetMapping(value = "/suggestions")
    ResponseEntity<FoodListResource> getFoodSuggestionsForTerm(@RequestParam(value = "searchTerm", required = true) String searchTerm);

    @GetMapping(value = "/units")
    ResponseEntity<FoodUnitList> getUnitList();

    @GetMapping(value = "/category/mappings")
    ResponseEntity<CategoryMappingListResource> getFoodCategoryMappings();

    @GetMapping(value = "/category")
    ResponseEntity<FoodCategoryListResource> getFoodCategories();

}
