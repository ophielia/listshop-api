package com.meg.atable.data.entity;

import com.meg.atable.api.model.TagType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
@GenericGenerator(
        name = "tag_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="tag_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class TagEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="tag_sequence")
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

    public TagEntity copy() {
        TagEntity copy = new TagEntity();
        copy.setName(getName());
        copy.setDescription(getDescription());
        copy.setSearchSelect(getSearchSelect());
        copy.setAssignSelect(getAssignSelect());
        copy.setPower(getPower());
            return copy;
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