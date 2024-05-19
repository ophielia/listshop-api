package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.v2.IngredientResource;

import java.util.List;

public class EmbeddedIngredientListResource {

    @JsonProperty("tagResourceList")
    private List<IngredientResource> ingredientResourceList;

    public EmbeddedIngredientListResource(List<IngredientResource> ingredientResourceList) {
        this.ingredientResourceList = ingredientResourceList;
    }

    public EmbeddedIngredientListResource() {
    }

    public List<IngredientResource> getIngredientResourceList() {
        return ingredientResourceList;
    }

    public void setIngredientResourceList(List<IngredientResource> tagResourceList) {
        this.ingredientResourceList = tagResourceList;
    }
}