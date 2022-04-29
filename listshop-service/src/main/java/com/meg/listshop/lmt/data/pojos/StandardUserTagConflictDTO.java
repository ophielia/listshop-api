package com.meg.listshop.lmt.data.pojos;

public class StandardUserTagConflictDTO {

    private Long standardTagId;
    private Long userTagId;

    public StandardUserTagConflictDTO(Long standardTagId, Long userTagId) {
        this.standardTagId = standardTagId;
        this.userTagId = userTagId;
    }

    public Long getStandardTagId() {
        return standardTagId;
    }

    public void setStandardTagId(Long standardTagId) {
        this.standardTagId = standardTagId;
    }

    public Long getUserTagId() {
        return userTagId;
    }

    public void setUserTagId(Long userTagId) {
        this.userTagId = userTagId;
    }
}
