package com.meg.listshop.lmt.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "food_entry")
public class FoodEntryEntity {

    @Id
    @Column(name = "entry_id")
    private Long entryId;

    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "category_id")
    private Long categoryId;

    private String name;
    private String marker;

    public FoodEntryEntity() {
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodEntryEntity that = (FoodEntryEntity) o;
        return Objects.equals(entryId, that.entryId) && Objects.equals(foodId, that.foodId) && Objects.equals(categoryId, that.categoryId) && Objects.equals(marker, that.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, foodId, categoryId, marker);
    }

    @Override
    public String toString() {
        return "FoodEntryEntity{" +
                "entryId=" + entryId +
                ", foodId=" + foodId +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", marker='" + marker + '\'' +
                '}';
    }
}