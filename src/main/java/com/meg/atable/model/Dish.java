package com.meg.atable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Dish {

    @JsonIgnore
    @ManyToOne
    private User user;

    @Id
    @GeneratedValue
    private Long id;

    private String dishName;

    private String description;

    @OneToMany
    private List<Tag> tags= new ArrayList<>();


    public Dish(User user, String dishName) {
        this.user=user;
        this.dishName = dishName;
    }

    public Dish(User user, String dishName, String description) {
        this.user=user;
        this.dishName = dishName;
        this.description = description;
    }

    public Dish() {
        // jpa empty constructor
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }


    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}