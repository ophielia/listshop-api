package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class AmountTextResource extends AbstractListShopResource implements ListShopModel {

    private String text;

    public AmountTextResource(String text) {
        this.text = text;
    }

    public AmountTextResource() {
    }

    public String getUnitDisplay() {
        return text;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "unit";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return "--";
    }
}