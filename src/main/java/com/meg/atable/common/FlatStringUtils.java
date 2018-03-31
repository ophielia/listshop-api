package com.meg.atable.common;

import com.meg.atable.service.TargetServiceConstants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public  class FlatStringUtils {

    public static Set<String> inflateStringToSet(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new HashSet<String>();
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
        return idList.stream().filter(i->!i.isEmpty()).map(i -> Long.valueOf(i)).collect(Collectors.toSet());
    }

    public static List<String> inflateStringToList(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new ArrayList<String>();
        }

        List<String> idList = new ArrayList<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList;
    }

    public static List<Long> inflateStringToLongList(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new ArrayList<Long>();
        }

        List<String> idList = new ArrayList<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
    }

    public static String flattenSetToString(Set<String> set, String delimiter) {
        return String.join(delimiter, set);
    }
}