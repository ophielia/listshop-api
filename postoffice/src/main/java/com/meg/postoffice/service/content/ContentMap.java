/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

package com.meg.postoffice.service.content;

import com.meg.postoffice.api.model.EmailParameters;

import java.util.*;

public class ContentMap extends AbstractMap<String, String> {

    private Map<String, String> entryMap;

    public static final String RECEIVER = "receiver";
    public static final String SENDER = "sender";
    public static final String SUBJECT = "subject";


    public ContentMap() {
        entryMap = new HashMap<>();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return entryMap.entrySet();
    }

    @Override
    public String put(String key, String value) {
        entryMap.put(key, value);

        return key;
    }


    public static ContentMap fromEmailParameters(EmailParameters emailParameters) {
        var contentMap = new ContentMap();
        // put set values in content map
        contentMap.put(RECEIVER, emailParameters.getReceiver());
        contentMap.put(SENDER, emailParameters.getSender());
        contentMap.put(SUBJECT, emailParameters.getSubject());

        // put parameters in content map
        for (Entry<String, List<String>> entry : emailParameters.getParameters().entrySet()) {
            List<String> parameterList = entry.getValue();
            if (parameterList == null || parameterList.isEmpty()) {
                continue;
            }
            if (parameterList.size() > 1) {
                addListToMap(contentMap, entry.getKey(), parameterList);
            } else {
                contentMap.put(entry.getKey(), parameterList.get(0));
            }
        }
        return contentMap;
    }

    private static void addListToMap(ContentMap contentMap, String key, List<String> parameterList) {
        // get size of parameter list
        var size = parameterList.size();
        contentMap.put(key + ".count", String.valueOf(size));
        var index = 0;
        for (String value : parameterList) {
            var indexKey = key + "." + index;
            contentMap.put(indexKey, value);
            index++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContentMap that = (ContentMap) o;
        return Objects.equals(entryMap, that.entryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entryMap);
    }
}
