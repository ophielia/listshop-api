package com.meg.atable.model;

import javax.persistence.*;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
public class TagRelation {

    @Id
    @GeneratedValue
    @Column(name = "tag_relation_id")
    Long id;

    @OneToOne
    private Tag parent;

    @OneToOne
    private Tag child;

    public TagRelation() {
        // no-arg constructor necessary
    }

    public TagRelation(Tag parent, Tag child) {
        this.parent = parent;
        this.child = child;
    }

    public Tag getParent() {
        return parent;
    }

    public void setParent(Tag parent) {
        this.parent = parent;
    }

    public Tag getChild() {
        return child;
    }

    public void setChild(Tag child) {
        this.child = child;
    }
}
