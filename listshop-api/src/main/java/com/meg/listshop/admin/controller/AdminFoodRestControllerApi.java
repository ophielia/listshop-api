/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.lmt.api.model.*;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/{tagId}/manual/factor")
    ResponseEntity<Object> assignManualFactorToTag(@PathVariable("tagId") Long tagId, @RequestBody PostFoodFactor factor);

    @DeleteMapping(value = "/{tagId}/manual/factor")
    ResponseEntity<Object> removeManualFactorFromTag(@PathVariable("tagId") Long tagId);

}
