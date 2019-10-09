package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.api.model.TagType;
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
        name = "tag-category-graph",
        attributeNodes = {
                @NamedAttributeNode("categories")
        }
)
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_sequence")
    @Column(name = "tag_id")
    private Long tag_id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private Boolean tagTypeDefault;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<DishEntity> dishes = new ArrayList<>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<ListLayoutCategoryEntity> categories = new ArrayList<>();

    private Boolean assignSelect;

    private Boolean searchSelect;

    private Boolean isVerified;

    private Double power;

    private Boolean toDelete = false;

    private Long replacementTagId;

    private Date createdOn;
    private Date updatedOn;
    private Date categoryUpdatedOn;
    private Date removedOn;

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
        // TODO - this is just for tests - make a DaoUtils so we can remove this method
        this.tag_id = tagId;
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

    public List<DishEntity> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishEntity> dishes) {
        this.dishes = dishes;
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

    public Boolean getAssignSelect() {
        return assignSelect;
    }

    public void setAssignSelect(Boolean assignSelect) {
        this.assignSelect = assignSelect;
    }

    public Boolean getSearchSelect() {
        return searchSelect;
    }

    public void setSearchSelect(Boolean searchSelect) {
        this.searchSelect = searchSelect;
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

    public TagEntity copy() {
        TagEntity copy = new TagEntity();
        copy.setName(getName());
        copy.setDescription(getDescription());
        copy.setSearchSelect(getSearchSelect());
        copy.setAssignSelect(getAssignSelect());
        copy.setPower(getPower());
        copy.setReplacementTagId(getReplacementTagId());
        copy.setToDelete(isToDelete());
        return copy;
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
        TagEntity tagEntity = (TagEntity) o;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tagType=" + tagType +
                ", tagTypeDefault=" + tagTypeDefault +
                ", dishes=" + dishes +
                ", categories=" + categories +
                ", assignSelect=" + assignSelect +
                ", searchSelect=" + searchSelect +
                ", isVerified=" + isVerified +
                ", power=" + power +
                ", childrenIds=" + childrenIds +
                ", parentId=" + parentId +
                '}';
    }

}