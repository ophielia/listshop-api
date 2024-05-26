package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Suggestion {

    @JsonProperty( "modifier_type")
    private String modifierType;
    private String text;
    @JsonProperty("reference_id")
    private String referenceId;

    public Suggestion() {
        // 4 jackson
    }

    public String getModifierType() {
        return modifierType;
    }

    public void setModifierType(String modifierType) {
        this.modifierType = modifierType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String modifierId) {
        this.referenceId = modifierId;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "modifierType='" + modifierType + '\'' +
                ", text='" + text + '\'' +
                ", modifierId='" + referenceId + '\'' +
                '}';
    }
}