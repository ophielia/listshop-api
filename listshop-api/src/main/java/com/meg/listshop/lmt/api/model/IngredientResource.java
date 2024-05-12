package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class IngredientResource extends AbstractListShopResource implements ListShopModel {

    private Ingredient ingredient;

    public IngredientResource(Ingredient tag) {
        this.ingredient = tag;
    }

    public IngredientResource() {
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "tag";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return ingredient.getId();
    }
}