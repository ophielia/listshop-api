package com.meg.listshop.conversion.tools;

public class RoundingUtils {

    public static double roundToHundredths(double value) {
        return Math.round(value * 100D) / 100D;
    }

    public static double roundToUnits(double value) {
        return Math.round(value);
    }

    public static double roundToThousandths(double value) {
        return Math.round(value * 1000D) / 1000D;
    }
}