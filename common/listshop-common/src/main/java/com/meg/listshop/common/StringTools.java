package com.meg.listshop.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringTools {
    private static final Logger logger = LoggerFactory.getLogger(StringTools.class);

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
            logger.error("received non-numeric string [{}]", string);
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

    public static boolean isEmail(String toTest) {
        if (toTest == null || toTest.isEmpty()) {
            return false;
        }
        return toTest.matches("(.*)@(.*)\\.\\w{2,}?$");
    }

    public static String safetyCheckTextInput(String textToCheck, Integer maxLength, Integer minLength, boolean toLower,
                                              boolean stripSpaces) {
        if (textToCheck == null) {
            return textToCheck;
        }
        if (textToCheck.length() > maxLength || textToCheck.length() < minLength) {
            return null;
        }

        textToCheck = textToCheck.replaceAll("\\p{Cc}", "");
        if (toLower) {
            textToCheck = textToCheck.replaceAll(" ", "");
        }

        if (stripSpaces) {
            textToCheck = textToCheck.toLowerCase();
        }

        return textToCheck;
    }

    public static String fillIfEmpty(String text, String replacement) {
        if (text == null || text.trim().isEmpty()) {
            return replacement;
        }
        return text;
    }

    public static boolean stringIsEmpty(String value) {
        if (value == null) {
            return true;
        }
        return value.isEmpty();
    }
}