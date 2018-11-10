package com.meg.atable.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by margaretmartin on 22/05/2017.
 */
@Entity
@Table(name="tag_relation")
@GenericGenerator(
        name = "tag_relation_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="tag_relation_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class TagRelationEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="tag_relation_sequence")
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
