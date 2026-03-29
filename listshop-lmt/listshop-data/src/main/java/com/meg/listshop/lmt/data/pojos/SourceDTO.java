/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.v2.SourceReferenceType;

public class SourceDTO {

    private Long referenceId;
    private String name;
    private SourceReferenceType sourceReferenceType;

    public SourceDTO(Long referenceId, String name) {
        this.referenceId = referenceId;
        this.name = name;
    }


   /* public SourceDTO(Long referenceId, String name,SourceReferenceType sourceReferenceType) {
        this.referenceId = referenceId;
        this.name = name;
        this.sourceReferenceType = sourceReferenceType;
    }*/

    public SourceDTO(Object referenceId, String name, String sourceReferenceType) {
        this.referenceId = referenceId instanceof Long ? (Long) referenceId : ((Number) referenceId).longValue();
        this.name = name;
        this.sourceReferenceType = sourceReferenceType != null ? SourceReferenceType.valueOf(sourceReferenceType) : null;
    }

    public void setReferenceType(SourceReferenceType sourceReferenceType) {
        this.sourceReferenceType = sourceReferenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getName() {
        return name;
    }

    public SourceReferenceType getReferenceType() {
        return sourceReferenceType;
    }

    @Override
    public String toString() {
        return "SourceDTO{" +
                "referenceId=" + referenceId +
                ", name='" + name + '\'' +
                ", referenceType=" + sourceReferenceType +
                '}';
    }
}
