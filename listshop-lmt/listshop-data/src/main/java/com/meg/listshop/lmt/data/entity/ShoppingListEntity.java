package com.meg.listshop.lmt.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list")
@NamedEntityGraph(
        name = "list-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("items")
        }
)

@NamedEntityGraph(
        name = "list-tag-entity-graph",
        attributeNodes = @NamedAttributeNode(value = "items", subgraph = "subgraph.tag"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.tag",
                        attributeNodes = @NamedAttributeNode(value = "tag"))})

public class ShoppingListEntity {

    @Id
    @Tsid
    @Column(name = "list_id")
    private Long listId;


    private Date createdOn;


    @Column(name = "list_types")
    private String listType;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", referencedColumnName = "list_id")
    private List<ListItemEntity> items = new ArrayList<>();

    @Column(name = "user_id")
    private Long userId;

    @JoinColumn(name = "list_layout_id")
    private Long listLayoutId;

    private Date lastUpdate;

    @Column(name = "meal_plan_id")
    private Long mealPlanId;

    private Boolean isStarterList;

    private String name;

    @Transient
    private List<DishEntity> dishSources = new ArrayList<>();

    @Transient
    private List<ShoppingListEntity> listSources = new ArrayList<>();

    @Transient
    private String mealPlanName = "";


    public ShoppingListEntity(Long id) {
        this.listId = id;
    }

    public ShoppingListEntity() {
        // empty constructor for jpa
    }


    public Long getId() {
        return listId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public List<ListItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ListItemEntity> items) {
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getListLayoutId() {
        return listLayoutId;
    }

    public void setListLayoutId(Long listLayoutId) {
        this.listLayoutId = listLayoutId;
    }

    public List<DishEntity> getDishSources() {
        return dishSources;
    }

    public void setDishSources(List<DishEntity> dishSources) {
        this.dishSources = dishSources;
    }

    public List<ShoppingListEntity> getListSources() {
        return listSources;
    }

    public void setListSources(List<ShoppingListEntity> listSources) {
        this.listSources = listSources;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(Long mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public String getMealPlanName() {
        return mealPlanName;
    }

    public void setMealPlanName(String mealPlanName) {
        this.mealPlanName = mealPlanName;
    }

    public Boolean getIsStarterList() {
        return isStarterList != null ? isStarterList : false;
    }

    public void setIsStarterList(Boolean starterList) {
        isStarterList = starterList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ShoppingListEntity{" +
                "listId=" + listId +
                ", name=" + name +
                ", isStarterList=" + isStarterList +
                ", createdOn=" + createdOn +
                ", listType=" + listType +
                ", userId=" + userId +
                ", listLayoutId=" + listLayoutId +
                ", lastUpdate=" + lastUpdate +
                ", mealPlanId=" + mealPlanId +
                ", dishSources=" + dishSources +
                ", listSources=" + listSources +
                ", mealPlanName='" + mealPlanName + '\'' +
                '}';
    }
}
