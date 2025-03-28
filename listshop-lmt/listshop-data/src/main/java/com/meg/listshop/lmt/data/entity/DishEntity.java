package com.meg.listshop.lmt.data.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "dish")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "filledDish",
                attributeNodes = {
                        @NamedAttributeNode(value = "items", subgraph = "itemWithTags")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "itemWithTags",
                                attributeNodes = {
                                        @NamedAttributeNode("tag")
                                }
                        )
                }
        )
})
public class DishEntity {

    @Column(name = "USER_ID")
    private Long userId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_sequence")
    @SequenceGenerator(name = "dish_sequence", sequenceName = "dish_sequence", allocationSize = 1)
    @Column(name = "dish_id")
    private Long dish_id;

    private String dishName;

    private String description;

    private String reference;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dish")
    private List<DishItemEntity> items = new ArrayList<>();
    private Date lastAdded;

    private Long autoTagStatus;

    private Date createdOn;

    public DishEntity(Long userId, String dishName) {
        this.userId = userId;
        this.dishName = dishName;
        this.createdOn = new Date();
    }

    public DishEntity(Long userId, String dishName, String description) {
        this.userId = userId;
        this.dishName = dishName;
        this.description = description;
        this.createdOn = new Date();
    }

    public DishEntity() {
        // jpa empty constructor
    }

    public Long getId() {
        return dish_id;
    }

    public void setId(Long dishId) {
        this.dish_id = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public List<DishItemEntity> getItems() {
        return items;
    }

    public void setItems(List<DishItemEntity> items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getLastAdded() {
        return lastAdded;
    }

    public void setLastAdded(Date lastAdded) {
        this.lastAdded = lastAdded;
    }

    public Long getAutoTagStatus() {
        return autoTagStatus;
    }

    public void setAutoTagStatus(Long autoTagStatus) {
        this.autoTagStatus = autoTagStatus;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void removeItems(List<DishItemEntity> dishItemsToRemove) {
        items.removeAll(dishItemsToRemove);
        dishItemsToRemove.forEach(d -> d.setDish(null));
    }
}