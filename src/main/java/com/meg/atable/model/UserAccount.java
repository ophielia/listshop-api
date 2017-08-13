package com.meg.atable.model;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserAccount {

    @Id
    @GeneratedValue
    @Column(name = "user_account_id")
    private Long id;

    @OneToMany(mappedBy = "userAccount")
    private Set<Dish> dishes = new HashSet<>();

    public String userName;

    private String password;

    public UserAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    UserAccount() {
        // jpa empty constructor
    }


    public Long getId() {
        return id;
    }


    public Set<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}