package com.meg.atable.api.model;

import com.meg.atable.data.entity.ShoppingListEntity;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public class ListResource extends ResourceSupport {
    public ListResource(ShoppingListEntity shoppingListEntity) {

    }
}
