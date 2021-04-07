package com.meg.listshop.lmt.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 25/05/2018.
 */
public class NewRawSlotResult {

    private final int rawMatchCount;
    private final int dishResultsPerSlot;
    private Integer slotNumber;
    private ArrayList<Long> filteredDishes = new ArrayList<>();
    private final List<DishTagSearchResult> dishSortedResults;
    private List<DishTagSearchResult> preSelectedResults;

    public NewRawSlotResult(Integer slotNumber, List<DishTagSearchResult> searchResults, int slotMatchCount, int dishResultsForSlot) {
        this.slotNumber = slotNumber;
        this.dishSortedResults = searchResults;
        this.rawMatchCount = slotMatchCount;
        this.dishResultsPerSlot = dishResultsForSlot;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public void clearFilteredDishes() {
        this.filteredDishes = new ArrayList<>();
    }

    public List<DishTagSearchResult> getFilteredMatches(int dishesPerSlot) {
        List<DishTagSearchResult> matches = new ArrayList<>();
        if (preSelectedResults != null) {
            matches.addAll(preSelectedResults);
        }
        for (DishTagSearchResult match : dishSortedResults) {
            if (matches.size() >= dishesPerSlot) {
                break;
            }
            if (!filteredDishes.contains(match.getDishId())) {
                matches.add(match);
            }
        }
        return matches;
    }

    public List<DishTagSearchResult> getFilteredMatches() {
        return getFilteredMatches(this.dishResultsPerSlot);
    }

    public void addDishesToFilter(List<DishTagSearchResult> dishMatches) {
        dishMatches.stream().forEach(m -> this.filteredDishes.add(m.getDishId()));
    }

    public void addDishIdsToFilter(List<Long> dishIdMatches) {
        this.filteredDishes.addAll(dishIdMatches);
    }

    public int getRawMatchCount() {
        return rawMatchCount;
    }

    public void addPrefilledDishIds(List<Long> currentDishIdsForSlot) {
        preSelectedResults = new ArrayList<>();
        for (DishTagSearchResult match : dishSortedResults) {
            if (currentDishIdsForSlot.contains(match.getDishId())) {
                preSelectedResults.add(match);
            }
        }
    }
}
