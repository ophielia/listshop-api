package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import org.springframework.hateoas.ResourceSupport;

public class CategoryResource extends ResourceSupport {

    private final ItemCategory category;

    public CategoryResource(ListLayoutCategoryEntity layoutCategory) {
        this.category = new ItemCategory(layoutCategory.getName(),
                layoutCategory.getId(),
                layoutCategory.getDisplayOrder(),
                CategoryType.Standard);
    }


    public ItemCategory getCategory() {
        return category;
    }
}