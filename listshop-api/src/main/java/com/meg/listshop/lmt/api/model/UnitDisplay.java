package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitDisplay {
    @JsonProperty("unit_id")
    private String unitId;
    private String display;

    public UnitDisplay() {
        // 4 jackson
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "UnitDisplay{" +
                "unitId='" + unitId + '\'' +
                ", display='" + display + '\'' +
                '}';
    }
}