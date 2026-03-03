package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class DishList {

    @JsonProperty("dish_list")
    private List<NestedDish> dishes;

    public DishList() {
    }

    public DishList(List<NestedDish> dishes) {
        this.dishes = dishes;
    }

    public List<NestedDish> getDishes() {
        return dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishList dishList = (DishList) o;
        return Objects.equals(dishes, dishList.dishes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dishes);
    }

    @Override
    public String toString() {
        return "DishList{" +
                "dishes=" + dishes +
                '}';
    }
}
