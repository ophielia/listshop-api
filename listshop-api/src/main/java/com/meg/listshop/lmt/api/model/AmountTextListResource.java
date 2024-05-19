package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AmountTextListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedStringList embeddedList;

    public AmountTextListResource(List<String> unitDisplayResources) {
        this.embeddedList = new EmbeddedStringList(unitDisplayResources);
    }

    public AmountTextListResource() {
    }

    public EmbeddedStringList getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedStringList embeddedList) {
        this.embeddedList = embeddedList;
    }

    @Override
    @JsonIgnore()
    public String getRootPath() {
        return "unit";
    }

    @Override
    @JsonIgnore()
    public String getResourceId() {
        return null;
    }
}
