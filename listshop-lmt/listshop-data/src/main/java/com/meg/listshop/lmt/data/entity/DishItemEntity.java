package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.FractionType;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "dish_items")
public class DishItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_item_sequence")
    @SequenceGenerator(name = "dish_item_sequence", sequenceName = "dish_item_sequence", allocationSize = 1)
    @Column(name = "dish_item_id")
    private Long dishItemId;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private DishEntity dish;

    @Column(name = "whole_quantity")
    private Integer wholeQuantity;

    @Column(name = "fractional_quantity")
    @Enumerated(EnumType.STRING)
    private FractionType fractionalQuantity;

    private Double quantity;

    @Column(name = "unit_id")
    private Long unitId;

    private String marker;

    @Column(name = "unit_size")
    private String unitSize;

    @Column(name = "raw_modifiers")
    private String rawModifiers;

    @Column(name = "modifiers_processed")
    private Boolean modifiersProcessed;

    @Column(name = "raw_entry")
    private String rawEntry;

    @Column(name = "user_size")
    private Boolean userSize;

    public DishItemEntity(Long id) {
        dishItemId = id;
    }

    public DishItemEntity() {
        // necessary for jpa construction
    }


    public Long getDishItemId() {
        return dishItemId;
    }

    public void setDishItemId(Long dishItemId) {
        this.dishItemId = dishItemId;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public DishEntity getDish() {
        return dish;
    }

    public void setDish(DishEntity dish) {
        this.dish = dish;
    }

    public String getRawModifiers() {
        return rawModifiers;
    }

    public void setRawModifiers(String rawModifiers) {
        this.rawModifiers = rawModifiers;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
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

    public FractionType getFractionalQuantity() {
        return fractionalQuantity;
    }

    public void setFractionalQuantity(FractionType fractionQuantity) {
        this.fractionalQuantity = fractionQuantity;
    }

    public Integer getWholeQuantity() {
        return wholeQuantity;
    }

    public void setWholeQuantity(Integer wholeQuantity) {
        this.wholeQuantity = wholeQuantity;
    }

    public Boolean getModifiersProcessed() {
        return modifiersProcessed;
    }

    public void setModifiersProcessed(Boolean modifiersProcessed) {
        this.modifiersProcessed = modifiersProcessed;
    }

    public String getRawEntry() {
        return rawEntry;
    }

    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    public Boolean getUserSize() {
        return userSize != null && userSize;
    }

    public void setUserSize(Boolean userSize) {
        this.userSize = userSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishItemEntity that = (DishItemEntity) o;
        return Objects.equals(dishItemId, that.dishItemId) && Objects.equals(tag, that.tag) && Objects.equals(dish, that.dish) && Objects.equals(quantity, that.quantity) && Objects.equals(unitId, that.unitId) && Objects.equals(marker, that.marker) && Objects.equals(unitSize, that.unitSize) && Objects.equals(rawModifiers, that.rawModifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishItemId, tag, dish, quantity, unitId, marker, unitSize, rawModifiers);
    }

    @Override
    public String toString() {
        return "DishItemEntity{" +
                "dishItemId=" + dishItemId +
                ", tag=" + tag +
                ", dish=" + dish +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionQuantity='" + fractionalQuantity + '\'' +
                ", quantity=" + quantity +
                ", unitId=" + unitId +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                ", rawModifiers='" + rawModifiers + '\'' +
                ", rawEntry='" + rawEntry + '\'' +
                ", modifiersProcessed='" + modifiersProcessed + '\'' +
                '}';
    }
}
