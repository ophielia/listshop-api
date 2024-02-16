package com.meg.listshop.lmt.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "food_category_mapping")
public class FoodCategoryTagMapping {

    @OneToOne
    @JoinColumn("categoryId")
    private FoodCategory category;

    @Column(name = "tag_id")
    private String categoryCode;

    private String name;

    public FoodCategoryTagMapping() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FoodCategory{" +
                "categoryId=" + categoryId +
                ", categoryCode='" + categoryCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}