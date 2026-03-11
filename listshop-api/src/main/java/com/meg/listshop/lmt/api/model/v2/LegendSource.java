/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LegendSource {

    @JsonProperty("list_id")
    private String referenceId;
    private String display;
    @JsonProperty("dish_id")
    private String referenceType;



    public LegendSource() {
        // empty constructor for jackson
    }

    public LegendSource(Long referenceId, String referenceType, String display) {
        this.referenceId = String.valueOf(referenceId);
        this.referenceType = referenceType;
        this.display = display;
    }

    @Override
    public String toString() {
        return "LegendSource{" +
                "referenceId='" + referenceId + '\'' +
                ", display='" + display + '\'' +
                ", referenceType=" + referenceType +
                '}';
    }
}
