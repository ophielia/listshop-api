package com.meg.atable.data.entity;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list")
public class ShoppingListEntity {

    @Id
    @GeneratedValue
    @Column(name = "list_id")
    private Long list_id;


    private Date createdOn;


    @Column(name = "list_types")
    @Enumerated(EnumType.STRING)
    private ListType listType;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "list_id", referencedColumnName = "list_id")
    private List<ItemEntity> items;

    @Column(name = "user_id")
    private Long userId;


    @Column(name = "list_layout_type")
    @Enumerated(EnumType.STRING)
    private ListLayoutType listLayoutType;

    public ShoppingListEntity(Long id) {
        this.list_id = id;
    }

    public ShoppingListEntity() {
        // empty constructor for jpa
    }


    public Long getId() {
        return list_id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public ListType getListType() {
        return listType;
    }

    public void setListType(ListType listType) {
        this.listType = listType;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ListLayoutType getListLayoutType() {
        return listLayoutType;
    }

    public void setListLayoutType(ListLayoutType listLayoutType) {
        this.listLayoutType = listLayoutType;
    }
}
