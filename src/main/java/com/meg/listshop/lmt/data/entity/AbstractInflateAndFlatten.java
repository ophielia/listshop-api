package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.lmt.service.TargetServiceConstants;

import java.util.List;
import java.util.Set;

/**
 * Created by margaretmartin on 05/01/2018.
 */
public class AbstractInflateAndFlatten {

    public List<String> inflateStringToList(String flatlist) {
        return inflateStringToList(flatlist, TargetServiceConstants.TARGET_TAG_DELIMITER);
    }

    public Set<String> inflateStringToSet(String flatlist) {
        return inflateStringToSet(flatlist, TargetServiceConstants.TARGET_TAG_DELIMITER);
    }

    public List<String> inflateStringToList(String flatlist, String delimiter) {
        return FlatStringUtils.inflateStringToList(flatlist,delimiter);
    }

    public Set<String> inflateStringToSet(String flatlist, String delimiter) {
        return FlatStringUtils.inflateStringToSet(flatlist,delimiter);
    }

    public String flattenListToString(List<String> list) {
        return String.join(TargetServiceConstants.TARGET_TAG_DELIMITER, list);
    }
}
