package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import org.springframework.hateoas.ResourceSupport;

public class CategoryResource extends ResourceSupport {

    private final ItemCategory category;

    public CategoryResource(ListLayoutCategoryEntity layoutCategory) {
        ItemCategory itemCategory = new ItemCategory(layoutCategory.getName(),
                layoutCategory.getId(),
                layoutCategory.getDisplayOrder(),
                CategoryType.Standard);
        this.category = itemCategory;
        ;
    }


    public ItemCategory getCategory() {
        return category;
    }
}