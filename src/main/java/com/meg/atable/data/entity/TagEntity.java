package com.meg.atable.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long tag_id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.LAZY)
    private List<DishEntity> dishes= new ArrayList<>();
    
    public TagEntity() {
        // jpa empty constructor
    }

    public TagEntity(String name, String description) {
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

    public List<DishEntity> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishEntity> dishes) {
        this.dishes = dishes;
    }
}