package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@Table(name="shadow_tags")
@GenericGenerator(
        name = "shadow_tags_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="shadow_tags_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ShadowTags {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="shadow_tags_sequence")
    private Long shadowTagId;

    private Long tagId;

    private Long dishId;

    public Long getId() {
        return shadowTagId;
    }

    public void setId(Long shadowTagId) {
        this.shadowTagId = shadowTagId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
}
