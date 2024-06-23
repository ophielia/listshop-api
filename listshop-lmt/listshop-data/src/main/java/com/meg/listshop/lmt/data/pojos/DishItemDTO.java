package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.api.model.FractionType;

import java.util.List;
import java.util.Objects;


public class DishItemDTO {

    private Long dishId;
    private Long dishItemId;
    private Long tagId;
    private Long unitId;
    private Double quantity;
    private Integer wholeQuantity;
    private FractionType fractionalQuantity;
    private String fractionDisplay;
    private String tagDisplay;
    private String unitDisplay;
    private List<String> rawModifiers;
    private String unitName;
    private String marker;
    private String unitSize;
    private String rawEntry;

    public DishItemDTO(Long dishId, Long dishItemId, Long tagId,
                       Long unitId, Double quantity, Integer wholeQuantity,
                       String fractionalQuantity, String tagDisplay,
                       String rawModifiersString, String unitName, String marker,
                       String unitSize, String rawEntry) {
        this.dishId = dishId;
        this.dishItemId = dishItemId;
        this.tagId = tagId;
        this.unitId = unitId;
        this.quantity = quantity;
        this.wholeQuantity = wholeQuantity;
        this.tagDisplay = tagDisplay;
        this.rawModifiers = FlatStringUtils.inflateStringToList(rawModifiersString,"|");
        this.unitName = unitName;
        this.marker = marker;
        this.unitSize = unitSize;
        this.rawEntry = rawEntry;
        if (fractionalQuantity != null) {
            this.fractionalQuantity = FractionType.valueOf(fractionalQuantity);
        }
    }

    public DishItemDTO() {
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Long getDishItemId() {
        return dishItemId;
    }

    public void setDishItemId(Long dishItemId) {
        this.dishItemId = dishItemId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
    }

    public FractionType getFractionalQuantity() {
        return fractionalQuantity;
    }

    public void setFractionalQuantity(FractionType fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
    }

    public List<String> getRawModifiers() {
        return rawModifiers;
    }

    public void setRawModifiers(List<String> rawModifiers) {
        this.rawModifiers = rawModifiers;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public String getFractionDisplay() {
        return fractionDisplay;
    }

    public void setFractionDisplay(String fractionDisplay) {
        this.fractionDisplay = fractionDisplay;
    }

    public String getTagDisplay() {
        return tagDisplay;
    }

    public void setTagDisplay(String tagDisplay) {
        this.tagDisplay = tagDisplay;
    }

    public String getUnitDisplay() {
        return unitDisplay;
    }

    public void setUnitDisplay(String unitDisplay) {
        this.unitDisplay = unitDisplay;
    }


    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    public String getRawEntry() {
        return rawEntry;
    }

    public boolean hasAmount() {
        return unitId != null
                && ( quantity != null || wholeQuantity != null || fractionalQuantity != null);
    }

    @Override
    public String toString() {
        return "DishItemDTO{" +
                "dishItemId=" + dishItemId +
                ", tagId=" + tagId +
                ", unitId=" + unitId +
                ", quantity=" + quantity +
                ", tagDisplay=" + tagDisplay +
                ", fractionDisplay=" + fractionDisplay +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", rawModifiers='" + rawModifiers + '\'' +
                ", unitName='" + unitName + '\'' +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishItemDTO that = (DishItemDTO) o;
        return Objects.equals(dishId, that.dishId) && Objects.equals(dishItemId, that.dishItemId) && Objects.equals(tagId, that.tagId) && Objects.equals(unitId, that.unitId) && Objects.equals(quantity, that.quantity) && Objects.equals(rawModifiers, that.rawModifiers) && Objects.equals(marker, that.marker) && Objects.equals(unitSize, that.unitSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId, dishItemId, tagId, unitId, quantity, rawModifiers, marker, unitSize);
    }

}