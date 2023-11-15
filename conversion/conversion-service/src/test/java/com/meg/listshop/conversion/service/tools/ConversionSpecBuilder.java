package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.service.ConversionSpec;

public class ConversionSpecBuilder {

    Long unitId;
    UnitType unitType;
    Unit buildingUnit = new Unit();

    public ConversionSpecBuilder() {
    }

    public ConversionSpecBuilder withUnitId(Long unitId) {
        this.buildingUnit.setId(unitId);
        return this;
    }

    public ConversionSpecBuilder withUnitType(UnitType type) {
        this.buildingUnit.setType(type);
        return this;
    }

    public ConversionSpecBuilder withFlavor(UnitFlavor flavor) {
        switch (flavor) {
            case Weight:
                buildingUnit.setWeight(true);
                break;
            case Volume:
                buildingUnit.setVolume(true);
                break;
            case DishUnit:
                buildingUnit.setDishUnit(true);
                break;
            case Liquid:
                buildingUnit.setLiquid(true);
                break;
            case ListUnit:
                buildingUnit.setLiquid(true);
        }

        return this;
    }

    public ConversionSpecBuilder withFlavors(UnitFlavor... flavors) {
        for (UnitFlavor flavor : flavors) {
            switch (flavor) {
                case Weight:
                    buildingUnit.setWeight(true);
                    break;
                case Volume:
                    buildingUnit.setVolume(true);
                    break;
                case DishUnit:
                    buildingUnit.setDishUnit(true);
                    break;
                case Liquid:
                    buildingUnit.setLiquid(true);
                    break;
                case ListUnit:
                    buildingUnit.setLiquid(true);
            }
        }
        return this;
    }

    public ConversionSpec build() {
        return ConversionSpec.fromExactUnit(buildingUnit);
    }
}
