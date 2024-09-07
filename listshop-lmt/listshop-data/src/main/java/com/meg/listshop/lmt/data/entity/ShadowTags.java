package com.meg.listshop.lmt.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@Table(name = "shadow_tags")
public class ShadowTags {

    @Id
    @Tsid
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
