package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.api.model.ListType;
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
public class ShoppingListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_sequence")
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

    @JoinColumn(name = "list_layout_id")
    private Long listLayoutId;

    private Date lastUpdate;

    @Column(name = "meal_plan_id")
    private Long mealPlanId;

    @Transient
    private List<DishEntity> dishSources = new ArrayList<>();

    @Transient
    private List<String> listSources = new ArrayList<>();

    @Transient
    private String mealPlanName = "";


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

    public List<String> getListSources() {
        return listSources;
    }

    public void setListSources(List<String> listSources) {
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

    @Override
    public String toString() {
        return "ShoppingListEntity{" +
                "list_id=" + list_id +
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
