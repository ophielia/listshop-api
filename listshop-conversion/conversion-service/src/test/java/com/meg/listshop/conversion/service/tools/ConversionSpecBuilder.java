package com.meg.listshop.conversion.service.tools;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.service.ConversionSpec;

public class ConversionSpecBuilder {

    Long unitId;
    UnitType unitType;
    UnitEntity buildingUnit = new UnitEntity();

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

    public ConversionSpecBuilder withUnitSubtype(UnitSubtype type) {
        this.buildingUnit.setSubtype(type);
        return this;
    }

    public ConversionSpecBuilder withFlavor(UnitFlavor flavor) {
        switch (flavor) {

            case DishUnit:
                buildingUnit.setDishUnit(true);
                break;
            case ListUnit:
                buildingUnit.setListUnit(true);
        }

        return this;
    }

    public ConversionSpec build() {

        return ConversionSpec.fromExactUnit(buildingUnit);
    }
}
