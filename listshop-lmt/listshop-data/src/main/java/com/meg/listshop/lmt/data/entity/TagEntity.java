package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.AdminTagFullInfo;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.TagInternalStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tag")
@NamedEntityGraph(
        name = "graph.TagCategory",
        attributeNodes = @NamedAttributeNode(value = "categories"))
@NamedNativeQuery(
        name = "TagEntity.findRatingByParent",
        query = "select t.tag_id from tag t " +
                "join tag_relation tr on tr.child_tag_id = t.tag_id " +
                "where tr.parent_tag_id = :rating_parent order by power"
)
@NamedNativeQuery(name = "IngredientsForDish",
        query = "select d.dish_id, dish_item_id , i.tag_id, i.unit_id, " +
                " quantity, whole_quantity , fractional_quantity, 'fractionDisplay' as fractionDisplay, " +
                " t.name as tagDisplay, i.raw_modifiers as raw_modifiers, u.name AS unit_name, i.marker, i.unit_size, " +
                " i.raw_entry " +
                "from dish d join dish_items i on i.dish_id = d.dish_id join tag t on t.tag_id = i.tag_id " +
                "left outer join units u on u.unit_id = i.unit_id " +
                "where d.dish_id = :dishId and t.tag_type = 'Ingredient'",
        resultSetMapping = "Mapping.DishItemDTO")
@SqlResultSetMapping(
        name = "Mapping.DishItemDTO",
        classes = {
                @ConstructorResult(
                        targetClass = com.meg.listshop.lmt.data.pojos.DishItemDTO.class,
                        columns = {
                                @ColumnResult(name = "dish_id", type = Long.class),
                                @ColumnResult(name = "dish_item_id", type = Long.class),
                                @ColumnResult(name = "tag_id", type = Long.class),
                                @ColumnResult(name = "unit_id", type = Long.class),
                                @ColumnResult(name = "quantity", type = Double.class),
                                @ColumnResult(name = "whole_quantity", type = Integer.class),
                                @ColumnResult(name = "fractional_quantity", type = String.class),
                                @ColumnResult(name = "tagDisplay", type = String.class),
                                @ColumnResult(name = "raw_modifiers", type = String.class),
                                @ColumnResult(name = "unit_name", type = String.class),
                                @ColumnResult(name = "marker", type = String.class),
                                @ColumnResult(name = "unit_size", type = String.class),
                                @ColumnResult(name = "raw_entry", type = String.class)
                        })})

public class TagEntity {

    @Id
    @Tsid
    @Column(name = "tag_id")
    private Long tag_id;

    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private Boolean tagTypeDefault;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<ListLayoutCategoryEntity> categories = new ArrayList<>();

    @Column(name = "is_group")
    private boolean isGroup;

    @Deprecated
    private Boolean isVerified;

    private Double power;

    private Boolean toDelete = false;

    private Long replacementTagId;

    private Date createdOn;
    private Date updatedOn;
    private Date categoryUpdatedOn;
    private Date removedOn;

    @Column(name = "internal_status")
    private Long internalStatus;

    @Column(name = "is_liquid")
    private Boolean isLiquid;

    @Column(name = "conversion_id")
    private Long conversionId;

    private String marker;
    @Transient
    private List<Long> childrenIds;

    @Transient
    private Long parentId;


    public TagEntity() {
        // jpa empty constructor
    }

    public TagEntity(String name, String description) {
        this.name = name;
        this.description = description;
        this.toDelete = false;
    }

    public TagEntity(Long tagId) {
        this.tag_id = tagId;
    }

    public Long getId() {
        return tag_id;
    }

    public void setId(Long tagId) {
        this.tag_id = tagId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Long> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<Long> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    public Boolean getTagTypeDefault() {
        return tagTypeDefault;
    }

    public void setTagTypeDefault(Boolean tagTypeDefault) {
        this.tagTypeDefault = tagTypeDefault;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Date getCategoryUpdatedOn() {
        return categoryUpdatedOn;
    }

    public void setCategoryUpdatedOn(Date categoryUpdatedOn) {
        this.categoryUpdatedOn = categoryUpdatedOn;
    }

    public Date getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(Date removedOn) {
        this.removedOn = removedOn;
    }

    public List<ListLayoutCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<ListLayoutCategoryEntity> categories) {
        this.categories = categories;
    }

    public Long getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(Long internalStatus) {
        this.internalStatus = internalStatus;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void setInternalStatus(TagInternalStatus status) {
        long newStatus = status.value();
        if (this.internalStatus == null) {
            this.internalStatus = 1L;
        }
        if (this.internalStatus % newStatus == 0) {
            return;
        }
        this.internalStatus = this.internalStatus * newStatus;
    }

    public Boolean getIsLiquid() {
        return isLiquid;
    }

    public void setIsLiquid(Boolean liquid) {
        isLiquid = liquid;
    }

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
    }

    public TagEntity copy() {
        var copy = new TagEntity();
        copy.setName(getName());
        copy.setDescription(getDescription());
        copy.setIsGroup(getIsGroup());
        copy.setPower(getPower());
        copy.setReplacementTagId(getReplacementTagId());
        copy.setToDelete(isToDelete());
        copy.setInternalStatus(getInternalStatus());
        copy.setConversionId(getConversionId());
        copy.setIsLiquid(getIsLiquid());
        return copy;
    }

    public void addCategory(ListLayoutCategoryEntity category) {
        this.categories.add(category);
        category.getTags().add(this);
    }

    public void removeCategory(ListLayoutCategoryEntity category) {
        this.categories.remove(category);
        category.getTags().remove(this);
    }

    public Boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public Long getReplacementTagId() {
        return replacementTagId;
    }

    public void setReplacementTagId(Long replacementTagId) {
        this.replacementTagId = replacementTagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var tagEntity = (TagEntity) o;
        return Objects.equals(tag_id, tagEntity.tag_id) &&
                Objects.equals(name, tagEntity.name) &&
                tagType == tagEntity.tagType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag_id, name, tagType);
    }

    @Override
    public String toString() {
        return "TagEntity{" +
                "tag_id=" + tag_id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tagType=" + tagType +
                ", tagTypeDefault=" + tagTypeDefault +
                ", categories=" + categories +
                ", isGroup=" + isGroup +
                ", power=" + power +
                ", toDelete=" + toDelete +
                ", replacementTagId=" + replacementTagId +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                ", categoryUpdatedOn=" + categoryUpdatedOn +
                ", removedOn=" + removedOn +
                ", internalStatus=" + internalStatus +
                ", isLiquid=" + isLiquid +
                ", conversionId=" + conversionId +
                '}';
    }


    public AdminTagFullInfo toAdminFullInfo() {
        AdminTagFullInfo fullInfo = new AdminTagFullInfo();
        fullInfo.setTagId(nullOrValueAsString(getId()));
        fullInfo.setName(getName());
        fullInfo.setUserId(nullOrValueAsString(getUserId()));
        fullInfo.setParentId(nullOrValueAsString(getParentId()));
        fullInfo.setParentName(getName());
        fullInfo.setDescription(getDescription());
        fullInfo.setTagType(getTagType().name());
        fullInfo.setGroup(getIsGroup());
        fullInfo.setPower(getPower());
        fullInfo.setToDelete(isToDelete());
        fullInfo.setConversionId(nullOrValueAsString(getConversionId()));
        fullInfo.setLiquid(getIsLiquid());

        return fullInfo;
    }

    private String nullOrValueAsString(Long longId) {
        if (longId == null) {
            return null;
        }
        return String.valueOf(longId);
    }
}