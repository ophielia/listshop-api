package com.meg.listshop.lmt.data.entity;

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

    @Column(name = "orig_whole_quantity")
    private Integer originalWholeQuantity;

    @Column(name = "orig_fractional_quantity")
    @Enumerated(EnumType.STRING)
    private FractionType originalFractionalQuantity;

    @Column(name = "orig_quantity")
    private Double originalQuantity;

    @Column(name = "orig_unit_id")
    private Long originalUnitId;

    private String marker;

    @Column(name = "unit_size")
    private String unitSize;

    @Column(name = "raw_entry")
    private String rawEntry;

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

    public Integer getOriginalWholeQuantity() {
        return originalWholeQuantity;
    }

    public void setOriginalWholeQuantity(Integer originalWholeQuantity) {
        this.originalWholeQuantity = originalWholeQuantity;
    }

    public FractionType getOriginalFractionalQuantity() {
        return originalFractionalQuantity;
    }

    public void setOriginalFractionalQuantity(FractionType originalFractionalQuantity) {
        this.originalFractionalQuantity = originalFractionalQuantity;
    }

    public Double getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(Double originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public Long getOriginalUnitId() {
        return originalUnitId;
    }

    public void setOriginalUnitId(Long originalUnitId) {
        this.originalUnitId = originalUnitId;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ListItemDetailEntity that = (ListItemDetailEntity) o;
        return Objects.equals(itemDetailId, that.itemDetailId) && Objects.equals(count, that.count) && Objects.equals(linkedListId, that.linkedListId) && Objects.equals(linkedDishId, that.linkedDishId) && Objects.equals(wholeQuantity, that.wholeQuantity) && fractionalQuantity == that.fractionalQuantity && Objects.equals(quantity, that.quantity) && Objects.equals(unitId, that.unitId) && Objects.equals(originalWholeQuantity, that.originalWholeQuantity) && originalFractionalQuantity == that.originalFractionalQuantity && Objects.equals(originalQuantity, that.originalQuantity) && Objects.equals(originalUnitId, that.originalUnitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemDetailId, count, linkedListId, linkedDishId, wholeQuantity, fractionalQuantity, quantity, unitId, originalWholeQuantity, originalFractionalQuantity, originalQuantity, originalUnitId);
    }

    @Override
    public String toString() {
        return "ListItemDetailEntity{" +
                "itemDetailId=" + itemDetailId +
                ", count=" + count +
                ", linkedListId=" + linkedListId +
                ", linkedDishId=" + linkedDishId +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", quantity=" + quantity +
                ", unitId=" + unitId +
                ", originalWholeQuantity=" + originalWholeQuantity +
                ", originalFractionalQuantity=" + originalFractionalQuantity +
                ", originalQuantity=" + originalQuantity +
                ", originalUnitId=" + originalUnitId +
                '}';
    }
}
