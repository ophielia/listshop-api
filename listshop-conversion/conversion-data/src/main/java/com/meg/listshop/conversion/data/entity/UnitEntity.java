package com.meg.listshop.conversion.data.entity;


import com.meg.listshop.conversion.data.pojo.UnitType;

public class UnitEntity {

    private Long id;

    private UnitType type;

    private boolean isLiquid;

    private boolean isListUnit;

    private boolean isDishUnit;

    private boolean isWeight;

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
}
