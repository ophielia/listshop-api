package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class NewTagResource extends Tag {
    private List<String> links = new ArrayList<>();

    @JsonProperty("_links")
    public List<String> getLinks() {
        return links;
    }

    public NewTagResource(Tag tag) {
        setTag_id(tag.getId());
        this.name(tag.getName())
                .description(tag.getDescription())
                .tagType(tag.getTagType())
                .ratingFamily(tag.getRatingFamily())
                .assignSelect(tag.getAssignSelect())
                .searchSelect(tag.getSearchSelect())
                .dishes(tag.getDishes())
                .power(tag.getPower())
                .parentId(tag.getParentId())
                .toDelete(tag.getToDelete());
    }


    public void setLinks(List<String> links) {
        this.links = links;
    }
}
