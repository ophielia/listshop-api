package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name="food_category_mapping")
@GenericGenerator(
        name = "food_category_mapping_seq",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="food_category_mapping_seq"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class FoodCategoryMapping {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="tag_relation_sequence")
    @Column(name = "food_category_mapping_id")
    Long id;

    @OneToOne
    private FoodCategory category;

    @OneToOne
    private TagEntity tag;

    public FoodCategoryMapping() {
        // no-arg constructor necessary
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodCategoryMapping that = (FoodCategoryMapping) o;
        return Objects.equals(id, that.id) && Objects.equals(category, that.category) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, tag);
    }

    @Override
    public String toString() {
        return "FoodCategoryMapping{" +
                "id=" + id +
                ", category=" + category +
                ", tag=" + tag +
                '}';
    }
}
