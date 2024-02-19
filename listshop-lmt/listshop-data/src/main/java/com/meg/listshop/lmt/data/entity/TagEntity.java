package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.TagType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tag")
@GenericGenerator(
        name = "tag_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "tag_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
@NamedEntityGraph(
        name = "graph.TagCategory",
        attributeNodes = @NamedAttributeNode(value = "categories"))
@NamedNativeQuery(
        name = "TagEntity.findRatingByParent",
        query = "select t.tag_id from tag t " +
                "join tag_relation tr on tr.child_tag_id = t.tag_id " +
                "where tr.parent_tag_id = :rating_parent order by power"
)
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_sequence")
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

    @Column(name = "food_id")
    private Long foodId;

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

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
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

    public Boolean getIsLiquid() {
        return isLiquid;
    }

    public void setIsLiquid(Boolean liquid) {
        isLiquid = liquid;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
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
        copy.setFoodId(getFoodId());
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
                ", foodId=" + foodId +
                '}';
    }


}