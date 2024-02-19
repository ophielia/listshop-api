package com.meg.listshop.conversion.service;

public interface ConversionFactorService {

    void deleteFactorsForTag(Long tagId);

    void addFactorForTag(Long tagId, double amount, Long unitId, double gramWeight);
}
