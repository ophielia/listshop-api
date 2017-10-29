package com.meg.atable.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dish")
public class DishEntity {

    @Column(name = "USER_ID")
    private Long userId;

    @Id
    @GeneratedValue
    @Column(name = "dish_id")
    private Long dish_id;

    private String dishName;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DISH_TAGS",
            joinColumns = @JoinColumn(name = "DISH_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    private List<TagEntity> tags= new ArrayList<>();


    public DishEntity(Long userId, String dishName) {
        this.userId = userId;
        this.dishName = dishName;
    }

    public DishEntity(Long userId, String dishName, String description) {
        this.userId = userId;
        this.dishName = dishName;
        this.description = description;
    }

    public DishEntity() {
        // jpa empty constructor
    }

    public Long getId() {
        return dish_id;
    }


    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
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
}