package com.meg.listshop.admin.controller;

import com.meg.listshop.lmt.api.model.LayoutCategoryListResource;
import com.meg.listshop.lmt.api.model.LayoutCategoryResource;
import com.meg.listshop.lmt.api.model.ModelMapper;
import com.meg.listshop.lmt.service.LayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@Controller
public class AdminLayoutRestController implements AdminLayoutRestControllerApi {


    private static final Logger logger = LoggerFactory.getLogger(AdminLayoutRestController.class);
    private final LayoutService layoutService;

    @Autowired
    AdminLayoutRestController(LayoutService layoutService) {
        this.layoutService = layoutService;
    }

    @Override
    public ResponseEntity<LayoutCategoryListResource> getStandardLayoutCategories() {
        List<LayoutCategoryResource> resourceList = layoutService.getDefaultCategories().stream()
                .map(ModelMapper::toModel)
                .map(LayoutCategoryResource::new)
                .collect(Collectors.toList());
        var returnValue = new LayoutCategoryListResource(resourceList);
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> assignStandardLayoutCategoryToTag(Long tagId, Long categoryId) {
        layoutService.moveTagToDefaultCategory(tagId, categoryId);
        return ResponseEntity.ok().build();
    }
}
