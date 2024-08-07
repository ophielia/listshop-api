package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.tools.ConversionTestTools;

import java.util.ArrayList;
import java.util.List;

public abstract class ConversionHandlerBuilder<T extends ConversionHandler> {

    List<ConversionFactor> factorList = new ArrayList<>();

    ConversionSpec fromSpec;
    ConversionSpec toSpec;
    ConversionFactorSource source;

    ConversionTargetType type;
    boolean oneWay;

    public ConversionHandlerBuilder() {
    }

    public T build() {
        return internalBuild(fromSpec, toSpec, factorList);

    }

    public abstract T internalBuild(ConversionSpec fromSpec, ConversionSpec toSpec, List<ConversionFactor> factorList);

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

    public ConversionHandlerBuilder withForScalar(ConversionTargetType conversionContextType) {
        type = conversionContextType;
        return this;
    }
}
