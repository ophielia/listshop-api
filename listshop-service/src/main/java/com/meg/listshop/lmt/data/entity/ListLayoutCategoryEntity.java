package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_category")
@GenericGenerator(
        name = "list_layout_category_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="list_layout_category_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ListLayoutCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_layout_category_sequence")
    @Column(name = "category_id")
    private Long categoryId;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_TAGS",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    private Set<TagEntity> tags = new HashSet<>();

    @Column(name = "layout_id")
    private Long layoutId;

    @Transient
    private final List<ItemEntity> items = new ArrayList<>();


    private Integer displayOrder;

    private Boolean isDefault;

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

    public Set<TagEntity> getTags() {
        return tags;
    }

    public void setTags(Set<TagEntity> tags) {
        this.tags = tags;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public void addItem(ItemEntity item) {
        this.items.add(item);
    }

    public List<ItemEntity> getItems() {
        return items;
    }




    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public void addTag(TagEntity tag) {
        this.tags.add(tag);
        tag.getCategories().add(this);
    }

    public void removeTag(TagEntity tag) {
        this.tags.remove(tag);
        tag.getCategories().remove(this);
    }
}
