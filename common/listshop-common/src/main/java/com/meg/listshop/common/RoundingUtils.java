package com.meg.listshop.common;

public class RoundingUtils {

    private RoundingUtils() {
    }

    public static double roundToHundredths(double value) {
        return RoundingType.HUNDREDTH.round(value);
    }

    public static double roundToUnits(double value) {
        return Math.round(value);
    }

    public static double roundToThousandths(double value) {
        return RoundingType.THOUSANDTH.round(value);
    }

    public static double roundToNearestFraction(double value) {
        double roundedToEights = RoundingUtils.round(value,RoundingType.EIGHTH);
        double roundedToThirds = RoundingUtils.round(value,RoundingType.THIRD);

        double eighthsDistance = Math.abs(value - roundedToEights);
        double thirdsDistance = Math.abs(value - roundedToThirds);

        if (eighthsDistance > thirdsDistance) {
            return RoundingUtils.round(roundedToThirds,RoundingType.THOUSANDTH);
        }

        return RoundingUtils.round(roundedToEights,RoundingType.THOUSANDTH);
    }

    public static double roundUpToNearestFraction(double value) {
        double roundedToEights = RoundingType.EIGHTH.roundUp(value);
        double roundedToThirds = RoundingType.THIRD.roundUp(value);

        double eighthsDistance = Math.abs(value - roundedToEights);
        double thirdsDistance = Math.abs(value - roundedToThirds);

        if (eighthsDistance > thirdsDistance) {
            return RoundingUtils.round(roundedToThirds,RoundingType.THOUSANDTH);
        }

        return RoundingUtils.round(roundedToEights,RoundingType.THOUSANDTH);
    }

    public static double round(double value, RoundingType roundingType) {
        return roundingType.round(value);
    }

    public static double doubleFromStringFraction(String fractionString) {
        if (!fractionString.contains("/")) {
            return 0.0;
        }
        String[] fractionParts = fractionString.split("/");
        if (fractionParts.length != 2) {
            return 0.0;
        }
        double numerator = Double.parseDouble(fractionParts[0].trim());
        double denominator = Math.max(Double.valueOf(fractionParts[1].trim()), 0.01);

        return numerator / denominator;
    }

    public static double roundUpToNearestWholeNumber(double quantity) {
        return Math.ceil(quantity);
    }
}
