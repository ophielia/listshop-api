package com.meg.atable.data.entity;

import com.meg.atable.api.model.TagType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long tag_id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private Boolean tagTypeDefault;

    private String ratingFamily;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<DishEntity> dishes = new ArrayList<>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<ListCategoryEntity> categories = new ArrayList<>();

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
    }

    public TagEntity(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Long getId() {
        return tag_id;
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

    public Long getTag_id() {
        return tag_id;
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

    public String getRatingFamily() {
        return ratingFamily;
    }

    public void setRatingFamily(String ratingFamily) {
        this.ratingFamily = ratingFamily;
    }
}