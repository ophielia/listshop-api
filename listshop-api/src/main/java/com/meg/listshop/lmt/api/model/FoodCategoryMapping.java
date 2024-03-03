package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class FoodCategoryMapping {

    @JsonProperty("tag_id")
    private String tagId;
    @JsonProperty("tag_name")
    private String tagName;

    @JsonProperty("food_category_id")
    private String foodCategoryId;

    @JsonProperty("food_category_name")
    private String foodCategoryName;

    public FoodCategoryMapping() {
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getFoodCategoryId() {
        return foodCategoryId;
    }

    public void setFoodCategoryId(String foodCategoryId) {
        this.foodCategoryId = foodCategoryId;
    }

    public String getFoodCategoryName() {
        return foodCategoryName;
    }

    public void setFoodCategoryName(String foodCategoryName) {
        this.foodCategoryName = foodCategoryName;
    }

    @Override
    public String toString() {
        return "FoodCategoryMapping{" +
                "tagId='" + tagId + '\'' +
                ", tagName='" + tagName + '\'' +
                ", foodCategoryId='" + foodCategoryId + '\'' +
                ", foodCategoryName='" + foodCategoryName + '\'' +
                '}';
    }
}
