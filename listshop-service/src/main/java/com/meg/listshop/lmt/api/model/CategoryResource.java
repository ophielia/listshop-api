package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.service.categories.ItemCategoryPojo;
import org.springframework.hateoas.ResourceSupport;

public class CategoryResource extends ResourceSupport {

    private final ItemCategoryPojo category;

    public CategoryResource(ListLayoutCategoryEntity layoutCategory) {
        this.category = new ItemCategoryPojo(layoutCategory.getName(),
                layoutCategory.getId(),
                layoutCategory.getDisplayOrder(),
                CategoryType.Standard);
    }


    public ItemCategoryPojo getCategory() {
        return category;
    }
}