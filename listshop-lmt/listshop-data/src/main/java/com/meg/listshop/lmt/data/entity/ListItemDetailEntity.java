package com.meg.listshop.lmt.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meg.listshop.lmt.api.model.FractionType;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item_details")

@NamedEntityGraph(
        name = "detail-item-tag-entity-graph",
        attributeNodes = @NamedAttributeNode(value = "item", subgraph = "subgraph.tag"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.tag",
                        attributeNodes = @NamedAttributeNode(value = "tag"))})

public class ListItemDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence", allocationSize = 1)
    @Column(name = "item_detail_id")
    private Long itemDetailId;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private ListItemEntity item;

    @Column(name = "used_count")
    private int count;

    @Column(name = "linked_list_id")
    private Long linkedListId;

    @Column(name = "linked_dish_id")
    private Long linkedDishId;

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

    @Column(name = "raw_entry")
    private String rawEntry;

    @Column(name = "nonspecified")
    private boolean unspecified = false;

    @Column(name = "contains_unspecified")
    private boolean containsUnspecified = false;


    @Column(name = "is_user_size")
    private boolean isUserSize = false;

    public ListItemDetailEntity() {
        // necessary for jpa construction
    }

    public ListItemEntity getItem() {
        return item;
    }

    public void setItem(ListItemEntity item) {
        this.item = item;
    }

    public Long getItemDetailId() {
        return itemDetailId;
    }

    public void setItemDetailId(Long itemDetailId) {
        this.itemDetailId = itemDetailId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getLinkedListId() {
        return linkedListId;
    }

    public void setLinkedListId(Long linkedListId) {
        this.linkedListId = linkedListId;
    }

    public Long getLinkedDishId() {
        return linkedDishId;
    }

    public void setLinkedDishId(Long linkedDishId) {
        this.linkedDishId = linkedDishId;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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

    public String getRawEntry() {
        return rawEntry;
    }

    public void setRawEntry(String rawEntry) {
        this.rawEntry = rawEntry;
    }

    public boolean isUnspecified() {
        return unspecified;
    }

    public void setUnspecified(boolean specified) {
        unspecified = specified;

    }

    public boolean isContainsUnspecified() {
        return containsUnspecified;
    }

    public void setContainsUnspecified(boolean containsUnspecified) {
        this.containsUnspecified = containsUnspecified;
    }

    public boolean isUserSize() {
        return isUserSize;
    }

    public void setUserSize(boolean userSize) {
        isUserSize = userSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ListItemDetailEntity that = (ListItemDetailEntity) o;
        return count == that.count && unspecified == that.unspecified && Objects.equals(itemDetailId, that.itemDetailId) && Objects.equals(item, that.item) && Objects.equals(linkedListId, that.linkedListId) && Objects.equals(linkedDishId, that.linkedDishId) && Objects.equals(quantity, that.quantity) && Objects.equals(unitId, that.unitId) && Objects.equals(unitSize, that.unitSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemDetailId, item, count, linkedListId, linkedDishId, quantity, unitId, unitSize, unspecified);
    }

    @Override
    public String toString() {
        return "ListItemDetailEntity{" +
                "itemDetailId=" + itemDetailId +
                ", item=" + item +
                ", count=" + count +
                ", linkedListId=" + linkedListId +
                ", linkedDishId=" + linkedDishId +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", quantity=" + quantity +
                ", unitId=" + unitId +
                ", marker='" + marker + '\'' +
                ", unitSize='" + unitSize + '\'' +
                ", rawEntry='" + rawEntry + '\'' +
                ", isUserSize='" + isUserSize + '\'' +
                ", containsUnspecified='" + containsUnspecified + '\'' +
                ", unspecified=" + unspecified +
                '}';
    }
}
