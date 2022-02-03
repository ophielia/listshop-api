/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.listshop.lmt.api.model.Dish;
import com.meg.listshop.lmt.api.model.Tag;

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
