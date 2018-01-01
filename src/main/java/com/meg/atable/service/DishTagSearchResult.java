package com.meg.atable.service;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class DishTagSearchResult {

    private final int targetTagLimit;
    private final Long dishId;
    private Integer targetTagMatchCount=0;
    private Integer slotTagMatchCount=0;
    private Boolean[] tagResults;

    public DishTagSearchResult(Long dishid, int targetTagLimit, int queriedTagSize) {
        this.dishId = dishid;
        this.targetTagLimit = targetTagLimit;
        this.tagResults = new Boolean[queriedTagSize] ;
    }

    public Long getDishId() {
        return dishId;
    }

    public int getTotalMatches() {
        return targetTagMatchCount + slotTagMatchCount;
    }

    public void addTagResult(int index, int foundFlag) {
        this.tagResults[index] = foundFlag>0;
        if (index < targetTagLimit) {
            targetTagMatchCount += foundFlag==0?0:1;
        } else {
            slotTagMatchCount += foundFlag==0?0:1;
        }
    }
}
