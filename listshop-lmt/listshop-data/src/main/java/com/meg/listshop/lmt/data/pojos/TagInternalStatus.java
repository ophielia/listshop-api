package com.meg.listshop.lmt.data.pojos;

public enum TagInternalStatus {

    EMPTY(3),
    CHECKED(3),
    LIQUID_ASSIGNED(5),
    FOOD_ASSIGNED(7),
    FOOD_VERIFIED(11),

    CATEGORY_ASSIGNED(13);

    private int value;

    TagInternalStatus (int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
