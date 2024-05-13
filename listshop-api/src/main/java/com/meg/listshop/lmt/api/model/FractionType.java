package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 09/10/2017.
 */

public enum FractionType {
    OneEighth("1/8"),
    OneQuarter("1/4"),
    ThreeEigths("3/8"),
    OneHalf("1/2"),
    FiveEighths("5/8"),
    ThreeQuarters("3/4"),
    SevenEighths("7/8"),
    OneThird("1/3"),
    TwoThirds("2/3");

    private final String display;


    FractionType(String displayName) {
        this.display = displayName;
    }

    public static Double doubleValueOf(FractionType fractionType) {
        switch (fractionType) {
            case OneEighth:
                return 0.125;
            case OneQuarter:
                return 0.25;
            case ThreeEigths:
                return 0.375;
            case OneHalf:
                return 0.5;
            case FiveEighths:
                return 0.625;
            case ThreeQuarters:
                return 0.75;
            case SevenEighths:
                return 0.875;
            case OneThird:
                return 0.333333;
            case TwoThirds:
                return 0.666666;
        }
        return 0.0;
    }

    public String getDisplayName() {
        return display;
    }


}
