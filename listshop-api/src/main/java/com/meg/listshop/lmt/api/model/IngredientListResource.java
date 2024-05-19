package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.v2.IngredientResource;

import java.util.List;

public class IngredientListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedIngredientListResource embeddedList;

    public IngredientListResource(List<IngredientResource> ingredientResourceList) {
        this.embeddedList = new EmbeddedIngredientListResource(ingredientResourceList);
    }

    public IngredientListResource() {
    }

    public EmbeddedIngredientListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedIngredientListResource embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "ingredient";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
