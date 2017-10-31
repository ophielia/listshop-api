package com.meg.atable.data.entity;

import com.meg.atable.api.model.ListLayoutType;

import javax.persistence.*;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_layout")
public class ListLayoutEntity {
    @Id
    @GeneratedValue
    @Column(name = "layout_id")
    private Long layoutId;


    @Column(name = "layout_type")
    @Enumerated(EnumType.STRING)
    private ListLayoutType layoutType;

    @OneToMany
    @JoinColumn(name = "layout_id", referencedColumnName = "layout_id")
    private List<ListCategoryEntity> categories;
}
