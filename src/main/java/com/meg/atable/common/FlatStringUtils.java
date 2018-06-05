package com.meg.atable.common;

import java.util.*;
import java.util.stream.Collectors;

public class FlatStringUtils {

    private FlatStringUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static Set<String> inflateStringToSet(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> idList = new HashSet<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList;
    }

    public static Set<Long> inflateStringToLongSet(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> idList = new HashSet<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList.stream().filter(i -> !i.isEmpty()).map(Long::valueOf).collect(Collectors.toSet());
    }

    public static List<String> inflateStringToList(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> idList = new ArrayList<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList;
    }

    public static List<Long> inflateStringToLongList(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> idList = new ArrayList<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public static String flattenSetToString(Set<String> set, String delimiter) {
        return String.join(delimiter, set);
    }

    public static String flattenListToString(List<String> list, String delimiter) {
        return String.join(delimiter, list);
    }
}