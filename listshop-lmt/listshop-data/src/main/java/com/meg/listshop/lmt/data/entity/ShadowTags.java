package com.meg.listshop.lmt.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@Table(name = "shadow_tags")
public class ShadowTags {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="shadow_tags_sequence")
    @SequenceGenerator(name = "shadow_tags_sequence", sequenceName = "shadow_tags_sequence", allocationSize = 1)
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
