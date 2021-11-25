package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list")
@GenericGenerator(
        name = "list_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "list_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
@NamedEntityGraph(
        name = "list-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("items")
        }
)
public class ShoppingListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_sequence")
    @Column(name = "list_id")
    private Long listId;


    private Date createdOn;


    @Column(name = "list_types")
    private String listType;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", referencedColumnName = "list_id")
    private List<ItemEntity> items = new ArrayList<>();

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
