package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class UnitDisplayResource extends AbstractListShopResource implements ListShopModel {

    private UnitDisplay unitDisplay;

    public UnitDisplayResource(UnitDisplay tag) {
        this.unitDisplay = tag;
    }

    public UnitDisplayResource() {
    }

    public UnitDisplay getUnitDisplay() {
        return unitDisplay;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "unit";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return unitDisplay.getUnitId();
    }
}