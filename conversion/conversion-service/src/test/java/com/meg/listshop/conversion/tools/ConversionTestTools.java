package com.meg.listshop.conversion.tools;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.UnitType;

public class ConversionTestTools {
    public static Unit makeImperialUnit(Long id, boolean isVolume) {
        Unit unit = new Unit();
        unit.setType(UnitType.Imperial);
        unit.setId(id);
        if (isVolume) {
            unit.setVolume(true);
        } else {
            unit.setWeight(true);
        }
        return unit;
    }

    public static Unit makeMetricUnit(Long id, boolean isVolume) {
        Unit unit = new Unit();
        unit.setType(UnitType.Metric);
        unit.setId(id);
        if (isVolume) {
            unit.setVolume(true);
        } else {
            unit.setWeight(true);
        }
        return unit;
    }
}