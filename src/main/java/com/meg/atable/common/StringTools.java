package com.meg.atable.common;

import java.util.List;

public class StringTools {

    private StringTools() {
        throw new IllegalAccessError("Utility class");
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
}