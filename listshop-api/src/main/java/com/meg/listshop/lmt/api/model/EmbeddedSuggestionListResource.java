package com.meg.listshop.lmt.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmbeddedSuggestionListResource {

    @JsonProperty("unit_displays")
    private List<Suggestion> unitDisplayResources;

    public EmbeddedSuggestionListResource(List<Suggestion> unitDisplayList) {
        this.unitDisplayResources = unitDisplayList;
    }

    public EmbeddedSuggestionListResource() {
    }

    @JsonIgnore
    public List<Suggestion> getUnitDisplayList() {
        return unitDisplayResources;
    }

    public void setUnitDisplayList(List<Suggestion> unitDisplayList) {
        this.unitDisplayResources = unitDisplayList;
    }
}