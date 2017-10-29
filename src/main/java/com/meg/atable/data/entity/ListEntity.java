package com.meg.atable.data.entity;

import com.meg.atable.api.model.ListType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list")
public class ListEntity {

    @Id
    @GeneratedValue
    @Column(name = "list_id")
    private Long list_id;


    private Date createdOn;

    private ListType listType;

    @OneToMany
    @JoinColumn(name = "list_id", referencedColumnName = "list_id")
    private List<ItemEntity> items;

    @Column(name = "user_id")
    private Long userId;


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
}
