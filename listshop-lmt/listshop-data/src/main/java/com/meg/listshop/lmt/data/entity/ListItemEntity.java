package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.api.model.FractionType;
import jakarta.persistence.*;

import java.util.*;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item")
public class ListItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence", allocationSize = 1)
    @Column(name = "item_id")
    private Long item_id;

    @OneToMany(mappedBy = "item")
    private List<ListItemDetailEntity> details = new ArrayList<>();

    @OneToOne( cascade = CascadeType.MERGE)
    @JoinColumn(name = "tagId",referencedColumnName = "tag_id")
    private TagEntity tag;

    @Column(name = "dish_sources")
    private String rawDishSources;

    @Column(name = "list_sources")
    private String rawListSources;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "used_count")
    private Integer usedCount;

    @Transient
    private Set<String> handles;


    private Date addedOn;

    private Date crossedOff;

    private Date removedOn;

    private Date updatedOn;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "whole_quantity")
    private Integer wholeQuantity;

    @Column(name = "fractional_quantity")
    @Enumerated(EnumType.STRING)
    private FractionType fractionalQuantity;

    @OneToOne( cascade = CascadeType.MERGE)
    @JoinColumn(name = "unit_id", referencedColumnName = "unit_id")
    private UnitEntity unit;

    @Column(name = "unit_size")
    private String unitSize;

    @Column(name = "amount_text")
    private String amountText;

    @Transient
    private Long tagId;

    public ListItemEntity(Long id) {
        item_id = id;
    }

    public ListItemEntity() {
        // necessary for jpa construction
    }

    public Long getId() {
        return item_id;
    }

    public void setId(Long itemId) {
        this.item_id = itemId;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public String getRawDishSources() {
        return rawDishSources;
    }

    public void setRawDishSources(String rawDishSources) {
        this.rawDishSources = rawDishSources;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public void setCrossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
    }

    public Date getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(Date removedOn) {
        this.removedOn = removedOn;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Integer getUsedCount() {
        return usedCount != null ? usedCount : 0;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Long getTagId() {
        return tagId;

    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }


    public String getRawListSources() {
        return rawListSources != null ? rawListSources : "";
    }

    public void setRawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
    }

    public Set<String> getHandles() {
        return handles != null ? handles : new HashSet<>();
    }

    public List<ListItemDetailEntity> getDetails() {
        return details;
    }

    public void setDetails(List<ListItemDetailEntity> details) {
        this.details = details;
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
        return Optional.of(unit).map(UnitEntity::getId).orElse(null);
    }

    public UnitEntity getUnit() {
        return unit;
    }

    public void setUnit(UnitEntity unit) {
        this.unit = unit;
    }

    public String getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(String unitSize) {
        this.unitSize = unitSize;
    }


    public String getAmountText() {
        return amountText;
    }

    public void setAmountText(String amountDescription) {
        this.amountText = amountDescription;
    }


    @Override
    public String toString() {
        return "ListItemEntity{" +
                "item_id=" + item_id +
                ", details=" + details +
                ", tag=" + tag +
                ", rawDishSources='" + rawDishSources + '\'' +
                ", rawListSources='" + rawListSources + '\'' +
                ", listId=" + listId +
                ", usedCount=" + usedCount +
                ", handles=" + handles +
                ", addedOn=" + addedOn +
                ", crossedOff=" + crossedOff +
                ", removedOn=" + removedOn +
                ", updatedOn=" + updatedOn +
                ", wholeQuantity=" + wholeQuantity +
                ", fractionalQuantity=" + fractionalQuantity +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", unitSize='" + unitSize + '\'' +
                ", amountDescription='" + amountText + '\'' +
                ", tagId=" + tagId +
                '}';
    }

    public ListItemEntity createCopy() {
        ListItemEntity cloned = new ListItemEntity();
        cloned.setAddedOn(this.getAddedOn());
        cloned.setCrossedOff(this.getCrossedOff());
        cloned.setRemovedOn(this.getRemovedOn());
        cloned.setUpdatedOn(this.getUpdatedOn());
        cloned.setUsedCount(this.getUsedCount());
        cloned.setListId(this.getListId());
        cloned.setRawDishSources(this.getRawDishSources());
        cloned.setRawListSources(this.getRawListSources());
        cloned.setTag(this.getTag());
        return cloned;
    }

    public String getDisplay() {
            return this.tag.getName();
    }

    public void addDetailToItem(ListItemDetailEntity detail) {
        details.add(detail);
    }

    public  int getDetailCount() {
        if (getDetails() == null || getDetails().isEmpty()) {
            return 0;
        }
        return getDetails().size();
    }
}
