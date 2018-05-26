package com.meg.atable.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by margaretmartin on 25/05/2018.
 */
public class NewRawSlotResult {

    private final Integer slotNumber;
    private final List<String> tagListForSlot;
    private final ArrayList<Long> filteredDishes=new ArrayList<>();
    private List<DishTagSearchResult> dishSortedResults;
    private long rawMatchCount;

    public NewRawSlotResult(Integer slotNumber, List<DishTagSearchResult> searchResults,
                            List<String> tagListForSlot, int rawMatchCount) {
        this.slotNumber = slotNumber;
        this.tagListForSlot = tagListForSlot;
        //this.filteredDishes = new ArrayList<>();
        //this.alwaysExclude = new ArrayList<>();
        this.dishSortedResults = searchResults;

    }


    public long getRawMatchCount() {
        return rawMatchCount;
    }
}
