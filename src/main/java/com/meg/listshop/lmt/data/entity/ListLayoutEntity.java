package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.ListLayoutType;
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
                value="list_layout_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ListLayoutEntity {
    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="list_layout_sequence")
    @Column(name = "layout_id")
    private Long layoutId;

    private String name;

    @Column(name = "layout_type")
    @Enumerated(EnumType.STRING)
    private ListLayoutType layoutType;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "layout_id", referencedColumnName = "layout_id")
    private List<ListLayoutCategoryEntity> categories;

    public ListLayoutEntity(Long layoutId) {
        this.layoutId = layoutId;
    }

    public ListLayoutEntity() {
        // empty constructor for jpa
    }

    public Long getId() {
        return layoutId;
    }

    public ListLayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(ListLayoutType layoutType) {
        this.layoutType = layoutType;
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
}
