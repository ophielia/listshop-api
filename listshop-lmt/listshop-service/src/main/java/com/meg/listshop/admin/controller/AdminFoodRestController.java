/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.admin.controller;

import com.meg.listshop.common.ControllerUtils;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.FoodConversionEntity;
import com.meg.listshop.lmt.data.entity.FoodEntity;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.layout.LayoutService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class AdminFoodRestController implements AdminFoodRestControllerApi {

    private final TagService tagService;
    private final TagStructureService tagStructureService;
    private final FoodService foodService;

    private static final Logger logger = LoggerFactory.getLogger(AdminFoodRestController.class);
    private final LayoutService layoutService;

    @Autowired
    AdminFoodRestController(TagService tagService, TagStructureService tagStructureService,
                            FoodService foodService, @Qualifier("V2LayoutService") LayoutService layoutService) {
        this.tagStructureService = tagStructureService;
        this.tagService = tagService;
        this.foodService = foodService;
        this.layoutService = layoutService;
    }



    public ResponseEntity<FoodListResource> getFoodSuggestionsForTerm(@RequestParam(value = "searchTerm", required = true) String searchTerm) {
        List<FoodEntity> foodEntities = foodService.getSuggestedFoods(searchTerm);
        Map<Long, List<FoodConversionEntity>> conversionFactors = foodService.getFoodFactors(foodEntities);
        List<FoodResource> resourceList = new ArrayList<>();
        for (FoodEntity foodEntity : foodEntities) {
            List<FoodConversionEntity> factors = conversionFactors.get(foodEntity.getFoodId());
            Food food = ModelMapper.toModel(foodEntity, factors);
            resourceList.add(new FoodResource(food));
        }

        var returnValue = new FoodListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<FoodUnitList> getUnitList() {
        List<UnitEntity> units = foodService.getUnits();
        List<FoodUnit> unitList = units.stream()
                .map(ModelMapper::toModel)
                .collect(Collectors.toList());

        var returnValue = new FoodUnitList(unitList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }



    public ResponseEntity<CategoryMappingListResource> getFoodCategoryMappings() {

        List<FoodCategoryMappingResource> resourceList = foodService.getFoodCategoryMappings().stream()
                .map(ModelMapper::toModel)
                .map(FoodCategoryMappingResource::new)
                .collect(Collectors.toList());

        var returnValue = new CategoryMappingListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }


    public ResponseEntity<FoodCategoryListResource> getFoodCategories() {
        // @GetMapping(value = "/food/category")
        List<FoodCategoryResource> resourceList = foodService.getFoodCategories().stream()
                .map(ModelMapper::toModel)
                .map(FoodCategoryResource::new)
                .collect(Collectors.toList());
        var returnValue = new FoodCategoryListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    public ResponseEntity<Object> assignManualFactorToTag( Long tagId, @RequestBody PostFoodFactor factor) {
        if (factor == null) {
            throw new IllegalArgumentException("No factor provided");
        }
        Long fromUnitId = ControllerUtils.stringToLongOrDefault(factor.getFromUnitId(), null);
        Long toUnitId = ControllerUtils.stringToLongOrDefault(factor.getToUnitId(), null);
        Double fromQuantity = ControllerUtils.stringToDoubleOrDefault(factor.getFromQuantity(), null);
        Double toQuantity = ControllerUtils.stringToDoubleOrDefault(factor.getToQuantity(), null);
        if (fromQuantity == null || toQuantity == null
        || fromUnitId == null || toUnitId == null) {
            throw new IllegalArgumentException("No quantity or unit provided");
        }
          if (fromQuantity == 0) {
              throw new IllegalArgumentException("From Quantity cannot be zero");
          }
        foodService.assignFactorToTag(tagId,fromUnitId, fromQuantity, toUnitId, toQuantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Object> removeManualFactorFromTag( Long tagId) {
        foodService.removeManualFactors(tagId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
