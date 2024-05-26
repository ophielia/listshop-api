package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SuggestionListResource extends AbstractListShopResource implements ListShopModel {

    @JsonProperty("_embedded")
    private EmbeddedSuggestionListResource embeddedList;

    public SuggestionListResource(List<Suggestion> unitDisplayResources) {
        this.embeddedList = new EmbeddedSuggestionListResource(unitDisplayResources);
    }

    public SuggestionListResource() {
    }

    public EmbeddedSuggestionListResource getEmbeddedList() {
        return embeddedList;
    }

    public void setEmbeddedList(EmbeddedSuggestionListResource embeddedList) {
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
