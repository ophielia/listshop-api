package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.repository.ConversionFactorSource;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;

import java.util.ArrayList;
import java.util.List;

public class ConversionFactorSourceBuilder {


    List<ConversionFactor> factorList = new ArrayList<>();

    public ConversionFactorSourceBuilder() {
    }

    public ConversionFactorSourceBuilder withFactor(Unit fromUnit, Unit toUnit, double factor) {
        ConversionFactor factorToAdd = SimpleConversionFactor.conversionFactor(fromUnit, toUnit, factor);
        this.factorList.add(factorToAdd);
        return this;
    }


    public ConversionFactorSource build() {


        return new TestConversionFactorSource(factorList);
    }
}
