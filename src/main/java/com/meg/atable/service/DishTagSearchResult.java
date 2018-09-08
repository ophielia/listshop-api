package com.meg.atable.service;

import com.meg.atable.common.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class DishTagSearchResult {

    private final int targetTagLimit;
    private final Long dishId;
    private final Date lastAdded;
    private Integer targetTagMatchCount=0;
    private Integer slotTagMatchCount=0;
    private Boolean[] tagResults;

    public DishTagSearchResult(Long dishid, Date lastAdded, int targetTagLimit, int queriedTagSize) {
        this.dishId = dishid;
        this.targetTagLimit = targetTagLimit;
        if (lastAdded == null) {
            lastAdded = DateUtils.asDate(LocalDate.of(1970, 10, 1));
        }
        this.lastAdded = lastAdded;
        this.tagResults = new Boolean[queriedTagSize] ;
    }

    public Long getDishId() {
        return dishId;
    }

    public int getTotalMatches() {
        return targetTagMatchCount + slotTagMatchCount;
    }

    public Date getLastAdded() {
        return lastAdded;
    }

    public int getSlotMatches() {
        return slotTagMatchCount;
    }

    public void addTagResult(int index, int foundFlag) {
        this.tagResults[index] = foundFlag>0;
        if (index < targetTagLimit) {
            targetTagMatchCount += foundFlag==0?0:1;
        } else {
            slotTagMatchCount += foundFlag==0?0:1;
        }
    }

    public Boolean[] getTagResults() {
        return tagResults;
    }

    public int getTargetTagLimit() {
        return targetTagLimit;
    }

    @Override
    public String toString() {
        return "DishTagSearchResult{" +
                "dishId=" + Long.toString(dishId) +
                '}';
    }

    public List<String> getMatchedTagIds(List<String> taglist) {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < tagResults.length;i++) {
            if (tagResults[i]) {
                matches.add(taglist.get(i));
            }
        }

        return matches;
    }
}
