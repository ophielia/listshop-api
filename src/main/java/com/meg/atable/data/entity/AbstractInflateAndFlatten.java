package com.meg.atable.data.entity;

import com.meg.atable.service.TargetServiceConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by margaretmartin on 05/01/2018.
 */
public class AbstractInflateAndFlatten {

    public List<String> inflateStringToList(String flatlist) {
        return inflateStringToList(flatlist, TargetServiceConstants.TARGET_TAG_DELIMITER);
    }

        public List<String> inflateStringToList(String flatlist, String delimiter) {
        if (flatlist == null || flatlist.isEmpty()) {
            return new ArrayList<String>();
        }

        List<String> idList = new ArrayList<>();
        idList.addAll(Arrays.asList(flatlist.split(delimiter)));
        return idList;
    }

    public String flattenListToString(List<String> list) {
        return String.join(TargetServiceConstants.TARGET_TAG_DELIMITER, list);
    }
}
