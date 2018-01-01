package com.meg.atable.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class RawSlotResult {
    private final Long slotId;
    private final int targetTagMatchLimit;
    private final int rawMatchCount;
    private final List<String> tagListForSlot;
    private HashMap<Long, DishTagSearchResult> dishResults;
    private ArrayList<DishTagSearchResult> dishSortedResults;

    public RawSlotResult(Long slot_id, int size, List<DishTagSearchResult> matches, List<DishTagSearchResult> emptyMatches, List<String> tagListForSlot) {
        this.slotId = slot_id;
        this.targetTagMatchLimit = size;
        this.rawMatchCount = matches.size();
        this.tagListForSlot = tagListForSlot;
        initiateSortedResults(matches, emptyMatches);
    }

    public int getRawMatchCount() {
        return this.rawMatchCount;
    }

    public Long getSlotId() {
        return slotId;
    }


    private void initiateSortedResults(List<DishTagSearchResult> matches, List<DishTagSearchResult> emptyMatches) {
        this.dishSortedResults = new ArrayList<>();
        // sort matches by total count, slot count and date (asc)
        matches.sort(Comparator.comparing(DishTagSearchResult::getTotalMatches)
                .thenComparing(DishTagSearchResult::getSlotMatches) //);
                .thenComparing((a, b) -> b.getLastAdded().compareTo(a.getLastAdded())).reversed());

        this.dishSortedResults.addAll(matches);
        this.dishSortedResults.addAll(emptyMatches);

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

}
