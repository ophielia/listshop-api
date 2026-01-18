package com.meg.listshop.common;

public class CommonUtils {

    public static <T> T elvis(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

}
