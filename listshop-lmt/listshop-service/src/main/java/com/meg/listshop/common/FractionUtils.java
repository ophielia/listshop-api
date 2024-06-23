package com.meg.listshop.common;

import com.meg.listshop.lmt.api.model.FractionType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FractionUtils {


    public static FractionType getFractionTypeForDecimal(BigDecimal decimalPart) {
        if (decimalPart.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        List<BigDecimal> decimalValues = Arrays.stream(FractionType.values())
                .map(  FractionType::doubleValueOf)
                .map(d -> BigDecimal.valueOf(d))
                .sorted()
                .collect(Collectors.toList());
        BigDecimal closestFraction = FractionUtils.closestInList(decimalPart,decimalValues);
        double fractionAsDouble = closestFraction.doubleValue();
        return FractionType.fromDouble(fractionAsDouble);
    }
    public static BigDecimal closestInList(BigDecimal target, List<BigDecimal> searchList) {
        int n = searchList.size();

        // Corner cases
        if (target.compareTo(searchList.get(0)) <= 0)
            return searchList.get(0);
        if (target.compareTo(searchList.get(n-1)) >= 0)
            return searchList.get(n-1);

        // Doing binary search
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;

            if (searchList.get(mid).compareTo(target) == 0)
                return searchList.get(mid);

			/* If target is less than array element,
			then search in left */
            if (target.compareTo(searchList.get(mid)) < 0) {

                // If target is greater than previous
                // to mid, return closest of two
                if (mid > 0 && target.compareTo(searchList.get(mid-1)) > 0)
                    return getClosest(searchList.get(mid - 1),
                            searchList.get(mid), target);

                /* Repeat for left half */
                j = mid;
            }

            // If target is greater than mid
            else {
                if (mid < n-1 && target.compareTo(searchList.get(mid+1)) < 0)
                    return getClosest(searchList.get(mid),searchList.get(mid+1),target);
                i = mid + 1; // update i
            }
        }

        // Only single element left after search
        return searchList.get(mid);




    }

    private static BigDecimal getClosest(BigDecimal val1, BigDecimal val2,
                                         BigDecimal target)
    {
        if (target.subtract(val1).compareTo(val2.subtract(target)) >= 0 )
            return val2;
        else
            return val1;
    }
}