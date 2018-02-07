package com.meg.atable.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_category")
@SequenceGenerator(name="list_layout_category_sequence", sequenceName = "list_layout_category_sequence")
public class ListLayoutCategoryEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="list_layout_category_sequence")
    @Column(name = "category_id")
    private Long categoryId;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_TAGS",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    private List<TagEntity> tags;

    @Column(name = "layout_id")
    private Long layoutId;

    public ListLayoutCategoryEntity(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ListLayoutCategoryEntity() {
        // empty constructor for jpa
    }

    public Long getId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }
}
