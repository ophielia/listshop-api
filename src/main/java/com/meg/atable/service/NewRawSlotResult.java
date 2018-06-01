package com.meg.atable.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by margaretmartin on 25/05/2018.
 */
public class NewRawSlotResult {

    private  Integer slotNumber;
    private final List<String> tagListForSlot;
    private  ArrayList<Long> filteredDishes=new ArrayList<>();
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

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void clearFilteredDishes() {
        this.filteredDishes = new ArrayList<>();
    }

    public List<DishTagSearchResult> getFilteredMatches(int dishesPerSlot) {
        List<DishTagSearchResult> matches = new ArrayList<>();
        for (DishTagSearchResult match : dishSortedResults) {
            if (matches.size() >= dishesPerSlot) {
                break;
            }
            if (filteredDishes.contains(match.getDishId())) {
                continue;
            }
            matches.add(match);
        }
        return matches;
    }

    public void addDishesToFilter(List<DishTagSearchResult> dishMatches) {
        dishMatches.stream().forEach(m -> this.filteredDishes.add(m.getDishId()));
    }
}
