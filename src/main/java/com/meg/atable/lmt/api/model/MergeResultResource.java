package com.meg.atable.lmt.api.model;


import com.meg.atable.lmt.data.entity.ShoppingListEntity;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class MergeResultResource extends ResourceSupport {

    private final MergeResult mergeResult;

    public MergeResultResource(MergeResult mergeResult, ShoppingListEntity shoppingListEntity, List<Category> categories) {
        ShoppingList shoppingList = ModelMapper.toModel(shoppingListEntity, categories);

        mergeResult.setShoppingList(shoppingList);
        this.mergeResult = mergeResult;
    }

    public MergeResult getMergeResult() {
        return mergeResult;
    }
}