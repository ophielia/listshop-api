package com.meg.listshop.lmt.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "foods")
public class FoodEntity {

    @Id
    @Column(name = "food_id")
    private Long foodId;


    @Column(name = "fdc_id")
    private Long fdcId;

    private String name;
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "has_factor")
    private Boolean hasFactor;
    public FoodEntity() {
    }

    public FoodEntity(Long foodId, Long fdcId, String name, Long categoryId, Boolean hasFactor) {
        this.foodId = foodId;
        this.fdcId = fdcId;
        this.name = name;
        this.categoryId = categoryId;
        this.hasFactor = hasFactor;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFdcId() {
        return fdcId;
    }

    public void setFdcId(Long fdcId) {
        this.fdcId = fdcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getHasFactor() {
        return hasFactor;
    }

    public void setHasFactor(Boolean hasFactor) {
        this.hasFactor = hasFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodEntity that = (FoodEntity) o;
        return Objects.equals(foodId, that.foodId) && Objects.equals(fdcId, that.fdcId) && Objects.equals(name, that.name) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodId, fdcId, name, categoryId);
    }

    @Override
    public String toString() {
        return "FoodEntity{" +
                "foodId=" + foodId +
                ", fdcId=" + fdcId +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}