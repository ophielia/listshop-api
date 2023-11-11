package com.meg.listshop.conversion.data.entity;

import com.meg.listshop.conversion.data.pojo.UnitType;

public class Unit {

    private Long id;

    private UnitType type;

    public Long getId() {
        return id;
    }

    public UnitType getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(UnitType type) {
        this.type = type;
    }
}
