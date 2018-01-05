package com.meg.atable.service;

import java.util.Map;

/**
 * Created by margaretmartin on 01/01/2018.
 */
public class AttemptResult {

    Integer[] attemptOrder;

    private Map<Integer, Double[]> slotStatistics;
    private double healthIndexMedian;
    private double healthIndexAverage;

    public AttemptResult(Integer[] order) {
        this.attemptOrder = order;
    }


    public void setSlotStatistics(Map<Integer, Double[]> slotStatistics) {
        this.slotStatistics = slotStatistics;
    }

    public Map<Integer, Double[]> getSlotStatistics() {
        return slotStatistics;
    }

    public void setHealthIndexMedian(double healthIndexMedian) {
        this.healthIndexMedian = healthIndexMedian;
    }

    public void setHealthIndexAverage(double healthIndexAverage) {
        this.healthIndexAverage = healthIndexAverage;
    }

    public double getHealthIndexMedian() {
        return healthIndexMedian;
    }

    public double getHealthIndexAverage() {
        return healthIndexAverage;
    }

    public Integer[] getAttemptOrder() {
        return attemptOrder;
    }

    @Override
    public String toString() {
        return "AttemptResult{" +
                ", healthIndexMedian=" + Double.toString(healthIndexMedian) +
                ", healthIndexAverage=" + healthIndexAverage +
                ", slotStatistics=" + slotStatistics +
                '}';
    }
}
