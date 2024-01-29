package com.meg.listshop.lmt.data.pojos;

public enum TagInternalStatus {

    CHECKED(3),
    LIQUID_ASSIGNED(5),
    FOOD_ASSIGNED(7),
    FOOD_VERIFIED(11);

    private int value;

    TagInternalStatus (int value) {
        this.value = value;
    }
}
