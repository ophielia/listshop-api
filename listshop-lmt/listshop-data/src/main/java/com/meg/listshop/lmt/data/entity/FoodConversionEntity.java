package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.conversion.service.FoodFactor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "food_conversions")
public class FoodConversionEntity implements FoodFactor {

    @Id
    @Column(name = "food_conversion_id")
    private Long id;

    @Column(name = "conversion_id")
    private Long conversionId;

    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "fdc_id")
    private Long fdcId;

    private String marker;

    private double amount;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "gram_weight")
    private double gramWeight;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "unit_size")
    private String unitSize;

    @Column(name = "unit_default")
    private Boolean unitDefault;

    public FoodConversionEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
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

    @Override
    public Long getReferenceId() {
        return id;
    }

    public double getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
    }

    public Long getFromUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    @Override
    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    @Override
    public Boolean getUnitDefault() {
        return unitDefault;
    }

    public void setUnitDefault(Boolean unitDefault) {
        this.unitDefault = unitDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodConversionEntity that = (FoodConversionEntity) o;
        return Double.compare(amount, that.amount) == 0 && Double.compare(gramWeight, that.gramWeight) == 0 && Objects.equals(id, that.id) && Objects.equals(conversionId, that.conversionId) && Objects.equals(foodId, that.foodId) && Objects.equals(fdcId, that.fdcId) && Objects.equals(marker, that.marker) && Objects.equals(unitName, that.unitName) && Objects.equals(unitId, that.unitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, conversionId, foodId, fdcId, marker, amount, unitName, gramWeight, unitId);
    }

    @Override
    public String toString() {
        return "FoodConversionEntity{" +
                "id=" + id +
                ", conversionId=" + conversionId +
                ", foodId=" + foodId +
                ", fdcId=" + fdcId +
                ", marker='" + marker + '\'' +
                ", amount=" + amount +
                ", unitName='" + unitName + '\'' +
                ", gramWeight=" + gramWeight +
                ", unitId=" + unitId +
                ", unitSize='" + unitSize + '\'' +
                ", unitDefault=" + unitDefault +
                '}';
    }
}