package com.meg.atable.data.entity;

import javax.persistence.*;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name = "category_relation")
@SequenceGenerator(name = "category_relation_sequence", sequenceName = "category_relation_sequence")
public class CategoryRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_relation_sequence")
    @Column(name = "category_relation_id")
    Long id;


    @OneToOne
    private ListLayoutCategoryEntity parent;

    @OneToOne
    private ListLayoutCategoryEntity child;

    public CategoryRelationEntity() {
        // no-arg constructor necessary
    }

    public CategoryRelationEntity(ListLayoutCategoryEntity parent, ListLayoutCategoryEntity child) {
        this.parent = parent;
        this.child = child;
    }

    public ListLayoutCategoryEntity getParent() {
        return parent;
    }

    public void setParent(ListLayoutCategoryEntity parent) {
        this.parent = parent;
    }

    public ListLayoutCategoryEntity getChild() {
        return child;
    }

    public void setChild(ListLayoutCategoryEntity child) {
        this.child = child;
    }
}
