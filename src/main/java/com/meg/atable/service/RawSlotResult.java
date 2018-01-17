package com.meg.atable.service;

import java.util.*;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class RawSlotResult {
    private final Long slotId;
    private final int rawMatchCount;
    private final List<String> tagListForSlot;
    private HashMap<Long, DishTagSearchResult> dishResults;
    private ArrayList<DishTagSearchResult> dishSortedResults;
    private List<Long> filteredDishes;
    private List<Long> alwaysExclude;
    private DishTagSearchResult preselectedDish;

    public RawSlotResult(Long slot_id, List<DishTagSearchResult> matches, List<DishTagSearchResult> targetMatches, List<DishTagSearchResult> emptyMatches, List<String> tagListForSlot) {
        this.slotId = slot_id;
        this.rawMatchCount = matches.size();
        this.tagListForSlot = tagListForSlot;
        this.filteredDishes = new ArrayList<>();
        this.alwaysExclude = new ArrayList<>();
        initiateSortedResults(matches, targetMatches, emptyMatches);
    }

    public int getRawMatchCount() {
        return this.rawMatchCount;
    }

    public Long getSlotId() {
        return slotId;
    }

    public List<String> getTagListForSlot() {
        return tagListForSlot;
    }

    private void initiateSortedResults(List<DishTagSearchResult> slotMatches, List<DishTagSearchResult> targetMatches, List<DishTagSearchResult> emptyMatches) {
        this.dishSortedResults = new ArrayList<>();
        slotMatches.addAll(targetMatches);
        // sort matches by slot count, total count and date (asc)
        slotMatches.sort(Comparator.comparing(DishTagSearchResult::getSlotMatches)
                .thenComparing(DishTagSearchResult::getTotalMatches) //);
                .thenComparing((a, b) -> b.getLastAdded().compareTo(a.getLastAdded())).reversed());
        this.dishSortedResults.addAll(slotMatches);
        this.dishSortedResults.addAll(emptyMatches);
        System.out.println("SlotResults: " + this.dishSortedResults.toString());

    }

    private void initiateResultHash(List<DishTagSearchResult> matches, List<DishTagSearchResult> emptyMatches) {
        this.dishResults = new HashMap();
        matches.stream().forEach(m -> {
            if (this.dishResults.containsKey(m.getDishId())) {
                System.out.print("oh");
            }
            this.dishResults.put(m.getDishId(), m);
        });
        emptyMatches.stream().forEach(m -> {
            if (this.dishResults.containsKey(m.getDishId())) {
                System.out.print("oh");
            }
            this.dishResults.put(m.getDishId(), m);
        });
    }

    public void clearFilteredDishes() {
        this.filteredDishes = new ArrayList<>();
    }

    public List<DishTagSearchResult> getFilteredMatches(int dishesPerSlot) {
        List<DishTagSearchResult> matches = new ArrayList<>();
        if (preselectedDish != null) {
            matches.add(preselectedDish);
        }
        for (DishTagSearchResult match : dishSortedResults) {
            if (matches.size() >= dishesPerSlot) {
                break;
            }
            if (filteredDishes.contains(match.getDishId())) {
                continue;
            }
            if (alwaysExclude.contains(match.getDishId())) {
                continue;
            }
            matches.add(match);
        }
        return matches;
    }

    public void addDishesToFilter(List<DishTagSearchResult> dishMatches) {
        dishMatches.stream().forEach(m -> this.filteredDishes.add(m.getDishId()));
    }


    public void addExcludesAndPresets(Map<Long, Long> slotToSelected) {
        if (slotToSelected != null) {
            for (Map.Entry<Long, Long> entry : slotToSelected.entrySet()) {
                if (slotId == entry.getKey().longValue()) {
                    this.preselectedDish = dishSortedResults.stream()
                            .filter(r -> r.getDishId() == entry.getValue().longValue())
                            .findFirst().get();
                    this.alwaysExclude.add(entry.getValue());
                } else {
                    this.alwaysExclude.add(entry.getValue());
                }
            }
        }
    }

    public void addDishIdsToFilter(List<Long> dishIdsInProposal) {
        this.filteredDishes.addAll(dishIdsInProposal);
    }
}
