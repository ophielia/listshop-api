package com.meg.atable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;



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
        return id;
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

}