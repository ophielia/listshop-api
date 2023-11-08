package com.meg.listshop.lmt.data.pojos;

public class LongTagIdPairDTO {

    private Long leftId;
    private Long rightId;

    public LongTagIdPairDTO(Long leftId, Long rightId) {
        this.leftId = leftId;
        this.rightId = rightId;
    }

    public Long getLeftId() {
        return leftId;
    }

    public void setLeftId(Long leftId) {
        this.leftId = leftId;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }
}
