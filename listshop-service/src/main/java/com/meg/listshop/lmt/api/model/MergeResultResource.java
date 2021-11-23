package com.meg.listshop.lmt.api.model;


import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.service.categories.ListShopCategory;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class MergeResultResource extends RepresentationModel {

    private final MergeResult mergeResult;

    public MergeResultResource(MergeResult mergeResult, ShoppingListEntity shoppingListEntity, List<ListShopCategory> categories) {
        ShoppingList shoppingList = ModelMapper.toModel(shoppingListEntity, categories);

        mergeResult.setShoppingList(shoppingList);
        this.mergeResult = mergeResult;
    }

    public MergeResult getMergeResult() {
        return mergeResult;
    }
}