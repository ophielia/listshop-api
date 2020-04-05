package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import org.springframework.hateoas.ResourceSupport;

public class ListItemRefreshResource extends ResourceSupport {

    private final ListItemRefresh listItemRefresh;

    public ListItemRefreshResource(ItemEntity itemEntity, ListLayoutCategoryEntity categoryEntity) {
        Item item = ModelMapper.toModel(itemEntity);
        Category category = ModelMapper.toModel(categoryEntity);

        this.listItemRefresh = new ListItemRefresh(item, category);
    }


    public ListItemRefresh getListItemRefresh() {
        return listItemRefresh;
    }
}