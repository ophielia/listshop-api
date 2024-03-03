package com.meg.listshop.lmt.data.pojos;

public enum TagInternalStatus {

    EMPTY(1),
    CHECKED(3),
    LIQUID_ASSIGNED(5),
    FOOD_ASSIGNED(7),
    FOOD_VERIFIED(11),

    CATEGORY_ASSIGNED(13);

    private final long value;

    TagInternalStatus(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }
}
