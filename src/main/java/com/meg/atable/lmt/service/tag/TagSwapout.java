package com.meg.atable.lmt.service.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 22/01/2018.
 */
public class TagSwapout {

    private HashMap<String,List<String>> searchToFound = new HashMap<>();

    public boolean contains(String tagId) {
            return searchToFound.containsKey(tagId);
    }

    public List<String> getAssignedValues(String tagId) {
        if (searchToFound.containsKey(tagId)) {
            return searchToFound.get(tagId);
        }
        return new ArrayList<>();
    }

    public void addSearchFound(String searchTag, String foundTag) {
        if (!searchToFound.containsKey(searchTag)) {
            searchToFound.put(searchTag,new ArrayList<>());
        }
        searchToFound.get(searchTag).add(foundTag);

    }
}
