package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "foods")
public class FoodEntity {

    @Id
    @Column(name = "food_id")
    private Long foodId;


    @Column(name = "fdc_id")
    private Long fdcId;

    @Column(name = "conversion_id")
    private Long conversionId;

    private String name;
    @Column(name = "original_name")
    private String originalName;
    @Column(name = "category_id")
    private Long categoryId;

    private String integral;
    private String marker;
    @Column(name = "has_factor")
    private Boolean hasFactor;
    public FoodEntity() {
    }

    public FoodEntity(Long foodId, Long fdcId, Long conversionId, String name, String originalName, Long categoryId, String integral, String marker, Boolean hasFactor) {
        this.foodId = foodId;
        this.fdcId = fdcId;
        this.conversionId = conversionId;
        this.name = name;
        this.originalName = originalName;
        this.categoryId = categoryId;
        this.integral = integral;
        this.marker = marker;
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

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Boolean getHasFactor() {
        return hasFactor;
    }

    public void setHasFactor(Boolean hasFactor) {
        this.hasFactor = hasFactor;
    }

    @Override
    public String toString() {
        return "FoodEntity{" +
                "foodId=" + foodId +
                ", fdcId=" + fdcId +
                ", conversionId=" + conversionId +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", categoryId=" + categoryId +
                ", integral='" + integral + '\'' +
                ", marker='" + marker + '\'' +
                ", hasFactor=" + hasFactor +
                '}';
    }
}