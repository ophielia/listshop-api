package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;

import java.util.ArrayList;
import java.util.List;

public class ConversionFactorSourceBuilder {


    List<ConversionFactor> factorList = new ArrayList<>();

    public ConversionFactorSourceBuilder() {
    }

    public ConversionFactorSourceBuilder withFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor) {
        ConversionFactor factorToAdd = SimpleConversionFactor.conversionFactor(fromUnit, toUnit, factor);
        this.factorList.add(factorToAdd);
        return this;
    }


    public ConversionFactorSource build() {


        return new TestConversionFactorSource(factorList);
    }
}
