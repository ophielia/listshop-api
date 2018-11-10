package com.meg.atable.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by margaretmartin on 05/01/2018.
 */
public class TargetProposalDish {

    private Dish dish;

    private List<Tag> matchedTags;

    public TargetProposalDish(Long proposalDishId) {

    }

    public Dish getDish() {
        return dish;
    }

    @JsonProperty("matched_tags")
    public List<Tag> getMatchedTags() {
        return matchedTags;
    }

    public TargetProposalDish dish(Dish dish) {
        this.dish = dish;
        return this;
    }

    public TargetProposalDish matchedTags(List<Tag> matchedTags) {
        this.matchedTags = matchedTags;
        return this;
    }
}
