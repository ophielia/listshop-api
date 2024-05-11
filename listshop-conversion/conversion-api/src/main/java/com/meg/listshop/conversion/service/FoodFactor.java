package com.meg.listshop.conversion.service;

public interface FoodFactor {

    Long getReferenceId();

    double getGramWeight();

    double getAmount();

    Long getFromUnitId();

    String getMarker();

    String getUnitSize();

    Boolean getUnitDefault();
}
