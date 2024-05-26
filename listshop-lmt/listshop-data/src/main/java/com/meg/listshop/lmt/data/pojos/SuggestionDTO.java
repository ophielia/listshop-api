package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.ModifierType;

import java.util.Objects;


public class SuggestionDTO {

    private ModifierType modifierType;
    private String text;
    private Long referenceId;

    public SuggestionDTO(ModifierType modifierType, String text, Long referenceId) {
        this.modifierType = modifierType;
        this.text = text;
        this.referenceId = referenceId;
    }

    public SuggestionDTO(String modifierType, String text, Long referenceId) {
        this.modifierType = ModifierType.valueOf(modifierType);
        this.text = text;
        this.referenceId = referenceId;
    }

    public ModifierType getModifierType() {
        return modifierType;
    }

    public void setModifierType(ModifierType modifierType) {
        this.modifierType = modifierType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionDTO that = (SuggestionDTO) o;
        return modifierType == that.modifierType && Objects.equals(text, that.text) && Objects.equals(referenceId, that.referenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifierType, text, referenceId);
    }

    @Override
    public String toString() {
        return "SuggestionDTO{" +
                "modifierType=" + modifierType +
                ", text='" + text + '\'' +
                ", referenceId=" + referenceId +
                '}';
    }
}