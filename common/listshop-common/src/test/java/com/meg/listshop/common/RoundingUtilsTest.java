/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RoundingUtilsTest {

    @Test
    void testRoundUpToNearestFraction() {
        // Current behavior for 5.8750001
        double result = RoundingUtils.roundUpToNearestFraction(5.8750001);
        Assertions.assertEquals(6.0, result, 0.0);
    }

    @Test
    void testNewRoundingMethod() {
        // 5.8750001 rounded to thousandths is 5.875.
        // 5.875 is an EIGHTH (RoundingType).
        // So it should NOT be rounded up to 6.
        // It should probably return 5.875.
        double result1 = RoundingUtils.roundUpToNearestRoundingType(5.8750001);
        Assertions.assertEquals(5.875, result1, 0.0);

        // 5.876 rounded to thousandths is 5.876.
        // 5.876 is NOT any RoundingType (UNIT, EIGHTH, THIRD, QUARTER).
        // So it should be rounded up to the nearest fraction (6.0).
        double result2 = RoundingUtils.roundUpToNearestRoundingType(5.876);
        Assertions.assertEquals(6.0, result2, 0.0);

        // 5.875 is already an EIGHTH.
        double result3 = RoundingUtils.roundUpToNearestRoundingType(5.875);
        Assertions.assertEquals(5.875, result3, 0.0);

        // 6.0 is already a UNIT.
        double result4 = RoundingUtils.roundUpToNearestRoundingType(6.0);
        Assertions.assertEquals(6.0, result4, 0.0);

        // 0.3333333333333333 is a THIRD. Rounded to thousandths is 0.333.
        double result5 = RoundingUtils.roundUpToNearestRoundingType(0.3333333333333333);
        Assertions.assertEquals(0.333, result5, 0.0);

        // 0.334 is NOT a RoundingType. Rounded up to nearest (EIGHTH: 0.375, THIRD: 0.666).
        // Nearest is 0.375.
        double result6 = RoundingUtils.roundUpToNearestRoundingType(0.334);
        Assertions.assertEquals(0.375, result6, 0.0);
    }
}
