package com.meg.listshop.lmt.data.pojos;

public enum TagInternalStatus {

    EMPTY(3),
    CHECKED(5),
    LIQUID_ASSIGNED(7),
    FOOD_ASSIGNED(11),
    FOOD_VERIFIED(13),

    CATEGORY_ASSIGNED(17);

    private final long value;

    TagInternalStatus(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }
}
