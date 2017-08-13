package com.meg.atable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long tag_id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.LAZY)
    private List<Dish> dishes= new ArrayList<>();



    public Tag(String name) {
        this.name = name;
    }

    public Tag() {
        // jpa empty constructor
    }

    public Tag( String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}