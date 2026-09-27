/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LegendSource {

    @JsonProperty("related_id")
    private String referenceId;
    @JsonProperty
    private String display;
    @JsonProperty("source_type")
    private String referenceType;



    public LegendSource() {
        // empty constructor for jackson
    }

    public LegendSource(Long referenceId, String display, String referenceType) {
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
