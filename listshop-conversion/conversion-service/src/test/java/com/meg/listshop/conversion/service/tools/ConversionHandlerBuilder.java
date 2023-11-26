package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestConversionHandler;
import com.meg.listshop.conversion.service.handlers.TestOneWayHandler;
import com.meg.listshop.conversion.tools.ConversionTestTools;

import java.util.ArrayList;
import java.util.List;

public class ConversionHandlerBuilder {

    List<ConversionFactor> factorList = new ArrayList<>();

    ConversionSpec fromSpec;
    ConversionSpec toSpec;

    boolean oneWay;

    public ConversionHandlerBuilder() {
    }

    public ConversionHandler build() {

        if (oneWay) {
            ConversionFactorSource source = new TestConversionFactorSource(factorList, true);
            return new TestOneWayHandler(fromSpec, toSpec, source);
        } else {
            ConversionFactorSource source = new TestConversionFactorSource(factorList);
            return new TestConversionHandler(fromSpec, toSpec, source);
        }

    }

    public ConversionHandlerBuilder withFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor) {
        ConversionFactor newFactor = SimpleConversionFactor.conversionFactor(fromUnit, toUnit, factor);
        factorList.add(newFactor);
        return this;
    }

    public ConversionHandlerBuilder withFromSpec(UnitType unitType, UnitSubtype subtype, UnitFlavor... unitFlavors) {
        UnitEntity unit = ConversionTestTools.makeUnit(null, unitType, subtype, unitFlavors);
        this.fromSpec = ConversionSpec.fromExactUnit(unit);
        return this;
    }

    public ConversionHandlerBuilder withToSpec(UnitType unitType, UnitSubtype subtype, UnitFlavor... unitFlavors) {
        UnitEntity unit = ConversionTestTools.makeUnit(null, unitType, subtype, unitFlavors);
        this.toSpec = ConversionSpec.fromExactUnit(unit);
        return this;
    }

    public ConversionHandlerBuilder withOneWay() {
        this.oneWay = true;
        return this;
    }
}
