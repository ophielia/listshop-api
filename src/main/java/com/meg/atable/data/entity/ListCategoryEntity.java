package com.meg.atable.data.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_category")
public class ListCategoryEntity {

    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long categoryId;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_TAGS",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    private List<TagEntity> tags;
}
