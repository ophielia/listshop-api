package com.meg.atable.data.entity;

import javax.persistence.*;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@Table(name="shadow_tags")
@SequenceGenerator(name="shadow_tags_sequence", sequenceName = "shadow_tags_sequence")
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
