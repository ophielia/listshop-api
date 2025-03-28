package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_layout")
@NamedEntityGraph(
        name = "graph.LayoutCategoriesItems",
        attributeNodes = @NamedAttributeNode(value = "categories", subgraph = "tags"),
        subgraphs = @NamedSubgraph(name = "tags", attributeNodes = @NamedAttributeNode("tags")))
public class ListLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_layout_sequence")
    @SequenceGenerator(name = "list_layout_sequence", sequenceName = "list_layout_sequence", allocationSize = 1)
    @Column(name = "layout_id")
    private Long layoutId;

    private String name;


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "layout_id", referencedColumnName = "layout_id")
    private Set<ListLayoutCategoryEntity> categories;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_default")
    private Boolean isDefault;

    public ListLayoutEntity(Long layoutId) {
        this.layoutId = layoutId;
    }

    public ListLayoutEntity() {
        // empty constructor for jpa
    }

    public Long getId() {
        return layoutId;
    }

    public Set<ListLayoutCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<ListLayoutCategoryEntity> categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public void addCategory(ListLayoutCategoryEntity category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        this.categories.add(category);
    }

    public void removeCategory(ListLayoutCategoryEntity category) {
        this.categories.remove(category);
    }
}
