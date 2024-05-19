package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;

import java.util.List;

public class DishDTO {

    private DishEntity dish;
    private List<DishItemDTO> ingredients;
    private List<DishItemEntity> tags;
    private RatingUpdateInfo ratings;


    public DishDTO(DishEntity dish, List<DishItemDTO> ingredients, List<DishItemEntity> tags, RatingUpdateInfo ratings) {
        this.dish = dish;
        this.ingredients = ingredients;
        this.tags = tags;
        this.ratings = ratings;
    }

    public DishEntity getDish() {
        return dish;
    }

    public List<DishItemDTO> getIngredients() {
        return ingredients;
    }

    public List<DishItemEntity> getTags() {
        return tags;
    }

    public RatingUpdateInfo getRatings() {
        return ratings;
    }

    @Override
    public String toString() {
        return "DishDTO{" +
                "dish=" + dish +
                ", ingredients=" + ingredients +
                ", tags=" + tags +
                ", ratings=" + ratings +
                '}';
    }
}