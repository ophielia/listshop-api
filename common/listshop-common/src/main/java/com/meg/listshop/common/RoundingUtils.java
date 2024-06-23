package com.meg.listshop.common;

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

    public static double doubleFromStringFraction(String fractionString) {
        if (!fractionString.contains("/")) {
            return 0.0;
        }
        String[] fractionParts = fractionString.split("/");
        if (fractionParts.length != 2) {
            return 0.0;
        }
        double numerator = Double.valueOf(fractionParts[0].trim());
        double denominator = Math.max(Double.valueOf(fractionParts[1].trim()), 0.01);

        return numerator / denominator;
    }
}