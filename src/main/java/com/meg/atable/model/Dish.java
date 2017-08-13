package com.meg.atable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Dish {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;

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
    private List<Tag> tags= new ArrayList<>();


    public Dish(UserAccount user, String dishName) {
        this.userAccount = user;
        this.dishName = dishName;
    }

    public Dish(UserAccount user, String dishName, String description) {
        this.userAccount = user;
        this.dishName = dishName;
        this.description = description;
    }

    public Dish() {
        // jpa empty constructor
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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

    @JsonIgnore
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