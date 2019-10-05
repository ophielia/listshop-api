package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.api.model.TagType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tag_extended")
@Immutable
public class TagExtendedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long tag_id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    private Boolean tagTypeDefault;

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

    @Column(name = "parent_tag_id")
    private Long parentId;


    public TagExtendedEntity() {
        // jpa empty constructor
    }

    public Long getId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TagType getTagType() {
        return tagType;
    }

    public Boolean getTagTypeDefault() {
        return tagTypeDefault;
    }

    public Boolean getAssignSelect() {
        return assignSelect;
    }

    public Boolean getSearchSelect() {
        return searchSelect;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public Double getPower() {
        return power;
    }

    public Boolean getToDelete() {
        return toDelete;
    }

    public Long getReplacementTagId() {
        return replacementTagId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public Date getCategoryUpdatedOn() {
        return categoryUpdatedOn;
    }

    public Date getRemovedOn() {
        return removedOn;
    }

    public Long getParentId() {
        return parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagExtendedEntity tagEntity = (TagExtendedEntity) o;
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
                ", assignSelect=" + assignSelect +
                ", searchSelect=" + searchSelect +
                ", isVerified=" + isVerified +
                ", power=" + power +
                ", parentId=" + parentId +
                '}';
    }

}