package com.meg.listshop.lmt.list.state;

import java.util.function.BiPredicate;

public class DetailFilter {

    private DetailFilter() {
    }

    public static boolean bothNullOrMatch(Long left, Long right) {
        return bothNull().or(bothNotNull().and(doMatch())).test(left, right);
    }

    public static boolean bothNotNullAndMatch(Long left, Long right) {
        return bothNotNull().and(doMatch()).test(left, right);
    }

    private static BiPredicate<Long, Long> bothNotNull() {
        // lambda is closed by capturing low and high (its "closure")
        return (left, right) -> left != null && right != null;
    }

    private static BiPredicate<Long, Long> bothNull() {
        // lambda is closed by capturing low and high (its "closure")
        return (left, right) -> left == null && right == null;
    }

    private static BiPredicate<Long, Long> doMatch() {
        // lambda is closed by capturing low and high (its "closure")
        return Long::equals;
    }

}
