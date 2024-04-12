package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.TestConversionFactorSource;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.handlers.*;
import com.meg.listshop.conversion.tools.ConversionTestTools;

import java.util.ArrayList;
import java.util.List;

public  class ScalingConversionHandlerBuilder {

    List<ConversionFactor> factorList = new ArrayList<>();

    ConversionSpec fromSpec;
    ConversionSpec toSpec;
    ConversionFactorSource source;

    ConversionTargetType type;
    boolean oneWay;

    public ScalingConversionHandlerBuilder() {
    }

    public ScalingHandler build() {
        ConversionFactorSource factorSource = new TestConversionFactorSource(factorList);
        return new TestScalingHandler(fromSpec, toSpec, factorSource, type );

    }


    public ScalingConversionHandlerBuilder withFactor(UnitEntity fromUnit, UnitEntity toUnit, double factor) {
        ConversionFactor newFactor = SimpleConversionFactor.conversionFactor(fromUnit, toUnit, factor);
        factorList.add(newFactor);
        return this;
    }

    public ScalingConversionHandlerBuilder withFromSpec(UnitType unitType, UnitSubtype subtype, UnitFlavor... unitFlavors) {
        UnitEntity unit = ConversionTestTools.makeUnit(null, unitType, subtype, unitFlavors);
        this.fromSpec = ConversionSpec.fromExactUnit(unit);
        return this;
    }

    public ScalingConversionHandlerBuilder withToSpec(UnitType unitType, UnitSubtype subtype, UnitFlavor... unitFlavors) {
        UnitEntity unit = ConversionTestTools.makeUnit(null, unitType, subtype, unitFlavors);
        this.toSpec = ConversionSpec.fromExactUnit(unit);
        return this;
    }

    public ScalingConversionHandlerBuilder withForScalar(ConversionTargetType conversionContextType) {
        type = conversionContextType;
        return this;
    }

}
