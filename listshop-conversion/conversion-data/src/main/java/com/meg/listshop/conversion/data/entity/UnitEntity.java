package com.meg.listshop.conversion.data.entity;


import com.meg.listshop.conversion.data.pojo.UnitType;

import javax.persistence.*;

@Entity
@Table(name = "units")
public class UnitEntity {

    @Id
    @Column(name = "UNIT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UnitType type;

    @Column(name = "name")
    private String name;

    @Column(name = "IS_LIQUID")
    private boolean isLiquid;

    @Column(name = "IS_LIST_UNIT")
    private boolean isListUnit;

    @Column(name = "IS_DISH_UNIT")
    private boolean isDishUnit;

    @Column(name = "IS_WEIGHT")
    private boolean isWeight;

    @Column(name = "IS_VOLUME")
    private boolean isVolume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public boolean isLiquid() {
        return isLiquid;
    }

    public void setLiquid(boolean liquid) {
        isLiquid = liquid;
    }

    public boolean isListUnit() {
        return isListUnit;
    }

    public void setListUnit(boolean listUnit) {
        isListUnit = listUnit;
    }

    public boolean isDishUnit() {
        return isDishUnit;
    }

    public void setDishUnit(boolean dishUnit) {
        isDishUnit = dishUnit;
    }

    public boolean isWeight() {
        return isWeight;
    }

    public void setWeight(boolean weight) {
        isWeight = weight;
    }

    public boolean isVolume() {
        return isVolume;
    }

    public void setVolume(boolean volume) {
        isVolume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UnitEntity{" +
                "id=" + id +
                ", name=" + name +
                ", type=" + type +
                ", isLiquid=" + isLiquid +
                ", isListUnit=" + isListUnit +
                ", isDishUnit=" + isDishUnit +
                ", isWeight=" + isWeight +
                ", isVolume=" + isVolume +
                '}';
    }
}
