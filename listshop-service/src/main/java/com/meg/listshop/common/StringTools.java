package com.meg.listshop.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringTools {
    private static final Logger  logger = LoggerFactory.getLogger(StringTools.class);

    private StringTools() {
        throw new IllegalAccessError("Utility class");
    }

    public static String safeLink(Optional<Link> link) {
        if (link.isPresent()) {
            return link.get().getHref();
        }
        return "";
    }

    public static String makeUniqueName(String listName, List<String> similarNames) {
        int suffixStart = Math.max(1, similarNames.size());
        boolean nameFound = false;
        String nameAttempt = "";
        while (suffixStart < 100 && !nameFound) {
            nameAttempt = listName + " " + suffixStart;
            if (!similarNames.contains(nameAttempt.toLowerCase())) {
                nameFound = true;
            }
            suffixStart++;
        }
        return nameAttempt;
    }

    public static Long stringToLong(String string) {
        try {
            return Long.valueOf(string);
        } catch (NumberFormatException e) {
            logger.error("received non-numeric string [%s]", string);
            return -1L;
        }
    }

    public static List<Long> stringListToLongs(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new ArrayList<>();
        }
        return stringList.stream().map(t -> {
                    try {
                        return Long.valueOf(t);
                    } catch (NumberFormatException e) {
                        return 0L;
                    }
                }).filter(t -> t > 0)
                .collect(Collectors.toList());
    }
}