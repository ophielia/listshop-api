package com.meg.atable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
public class TagRelation {

    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    private Tag parent;

    @OneToOne
    private Tag child;

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
