package com.meg.atable.service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class ProposalAttempt {

    Integer[] attemptOrder;

    private Map<Integer, List<DishTagSearchResult>> dishMatches = new HashMap<Integer, List<DishTagSearchResult>>();
    private Map<Integer, Double[]> slotResults = new HashMap<Integer, Double[]>();
    private double healthIndexMedian;
    private double healthIndexAverage;

    public ProposalAttempt(Integer[] order) {
        this.attemptOrder = order;
    }

    public Integer[] getAttemptOrder() {
        return attemptOrder;
    }

    public void setDishMatches(int i, List<DishTagSearchResult> dishMatches) {
        int totalTagCount = dishMatches.get(0).getTagResults().length;
        int slotTagCount = totalTagCount - dishMatches.get(0).getTargetTagLimit();
        processStatistics(i, totalTagCount, slotTagCount, dishMatches);
        this.dishMatches.put(i, dishMatches);
    }

    private void processStatistics(int slotNumber, int totalTagCount, int slotTagCount, List<DishTagSearchResult> dishMatches) {
        int dishCount = dishMatches.size();

        int totalNonEmptyMatchCount = 0;
        int slotNonEmptyMatchCount = 0;
        List<Double> totalMatchPercentageList = new ArrayList<>();
        List<Double> slotMatchPercentageList = new ArrayList<>();
        for (DishTagSearchResult result : dishMatches) {
            // process statistics for this single run
            totalNonEmptyMatchCount += result.getTotalMatches() > 0 ? 1 : 0;
            slotNonEmptyMatchCount += result.getSlotMatches() > 0 ? 1 : 0;
            Double totalMatchPct = (double) result.getTotalMatches() / ((double) totalTagCount);
            totalMatchPercentageList.add(totalMatchPct);
            Double slotMatchPct = (double) result.getSlotMatches() / ((double) slotTagCount);
            slotMatchPercentageList.add(slotMatchPct);
        }

        double totalMatchExists = (double) totalNonEmptyMatchCount / (double) dishCount;
        double slotMatchExists = (double) slotNonEmptyMatchCount / (double) dishCount;

        double avgTotalMatch = totalMatchPercentageList.stream().mapToDouble(t -> t).average().getAsDouble();
        double avgSlotMatch = slotMatchPercentageList.stream().mapToDouble(t -> t).average().getAsDouble();

        double healthindex = 40.0 * slotMatchExists;
        healthindex += 15.0 * totalMatchExists;
        healthindex += 30.0 * avgSlotMatch;
        healthindex += 15.0 * avgTotalMatch;

        Double[] results = {healthindex, slotMatchExists, avgSlotMatch, totalMatchExists, avgTotalMatch};
        slotResults.put(slotNumber, results);

    }


    public AttemptResult finalizeResults() {
        AttemptResult result = new AttemptResult(getAttemptOrder());

        // just do health index
        List<Double> healthIndexList = slotResults.values().stream().map(v -> v[0]).collect(Collectors.toList());
        healthIndexList.stream().sorted();
        double median = 0;
        if (healthIndexList.size() % 2 == 0) {
            median = (healthIndexList.get(healthIndexList.size() / 2) +
                    healthIndexList.get(healthIndexList.size() / 2 - 1))
                    / 2.0;
        } else {
            median = healthIndexList.get((int) Math.ceil(healthIndexList.size() / 2));
        }
        setHealthIndexMedian(median);
        setHealthIndexAverage(healthIndexList.stream().mapToDouble(t -> t).average().getAsDouble());


        return result;


    }

    @Override
    public String toString() {
        return "ProposalAttempt{" +
                "attemptOrder=" + Arrays.toString(attemptOrder) +
                ", slotResults=" + slotResults +
                '}';
    }

    public void setHealthIndexMedian(double healthIndexMedian) {
        this.healthIndexMedian = healthIndexMedian;
    }

    public double getHealthIndexMedian() {
        return healthIndexMedian;
    }

    public void setHealthIndexAverage(double healthIndexAverage) {
        this.healthIndexAverage = healthIndexAverage;
    }

    public double getHealthIndexAverage() {
        return healthIndexAverage;
    }
}
