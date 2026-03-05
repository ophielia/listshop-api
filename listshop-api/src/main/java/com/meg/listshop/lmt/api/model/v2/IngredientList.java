package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IngredientList {

    @JsonProperty("ingredient_list")
    private List<Ingredient> ingredients;

    public IngredientList() {
        // empty constructor for Jackson
    }

    public IngredientList(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }


    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return "IngredientList{" +
                "ingredientList=" + ingredients +
                '}';
    }
}
