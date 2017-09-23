package com.meg.atable.data.entity;

import javax.persistence.*;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name="tag_relation")
public class TagRelationEntity {

    @Id
    @GeneratedValue
    @Column(name = "tag_relation_id")
    Long id;

    @OneToOne
    private TagEntity parent;

    @OneToOne
    private TagEntity child;

    public TagRelationEntity() {
        // no-arg constructor necessary
    }

    public TagRelationEntity(TagEntity parent, TagEntity child) {
        this.parent = parent;
        this.child = child;
    }

    public TagEntity getParent() {
        return parent;
    }

    public void setParent(TagEntity parent) {
        this.parent = parent;
    }

    public TagEntity getChild() {
        return child;
    }

    public void setChild(TagEntity child) {
        this.child = child;
    }
}
