package com.meg.listshop.lmt.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "food_conversions")
public class FoodConversionEntity {

    @Id
    @Column(name = "conversion_id")
    private Long conversionId;

    @Column(name = "food_id")
    private Long foodId;


    @Column(name = "fdc_id")
    private Long fdcId;

    private double amount;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "gram_weight")
    private double gramWeight;

    @Column(name = "unit_id")
    private Long unitId;

    public FoodConversionEntity() {
    }

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public double getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodConversionEntity that = (FoodConversionEntity) o;
        return Double.compare(amount, that.amount) == 0 && Double.compare(gramWeight, that.gramWeight) == 0 && Objects.equals(conversionId, that.conversionId) && Objects.equals(foodId, that.foodId) && Objects.equals(fdcId, that.fdcId) && Objects.equals(unitName, that.unitName) && Objects.equals(unitId, that.unitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversionId, foodId, fdcId, amount, unitName, gramWeight, unitId);
    }

    @Override
    public String toString() {
        return "FoodConversions{" +
                "conversionId=" + conversionId +
                ", foodId=" + foodId +
                ", fdcId=" + fdcId +
                ", amount=" + amount +
                ", unitName='" + unitName + '\'' +
                ", gramWeight=" + gramWeight +
                ", unitId=" + unitId +
                '}';
    }
}