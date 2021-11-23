package com.meg.listshop.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Optional;

public class StringTools {
    private static final Logger logger = LogManager.getLogger(StringTools.class);

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
            logger.error("received non-numeric string [" + string + "]");
            return -1L;
        }
    }
}