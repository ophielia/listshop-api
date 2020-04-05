package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name = "category_relation")
@GenericGenerator(
        name = "category_relation_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="category_relation_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value="1000"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
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

    public Long getId() {
        return id;
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
