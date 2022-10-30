package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_layout")
@GenericGenerator(
        name = "list_layout_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "list_layout_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ListLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_layout_sequence")
    @Column(name = "layout_id")
    private Long layoutId;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "layout_id", referencedColumnName = "layout_id")
    private List<ListLayoutCategoryEntity> categories;

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

    public List<ListLayoutCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<ListLayoutCategoryEntity> categories) {
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
}
