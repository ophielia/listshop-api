package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name = "food_category_mapping")
public class FoodCategoryMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "food_category_mapping_seq")
    @SequenceGenerator(name = "food_category_mapping_seq", sequenceName = "food_category_mapping_seq", allocationSize = 1)
    @Column(name = "food_category_mapping_id")
    Long id;


    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "tag_id")
    private Long tagId;

    public FoodCategoryMappingEntity() {
        // no-arg constructor necessary
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodCategoryMappingEntity that = (FoodCategoryMappingEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(categoryId, that.categoryId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryId, tagId);
    }

    @Override
    public String toString() {
        return "FoodCategoryMappingEntity{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", tagId=" + tagId +
                '}';
    }
}
