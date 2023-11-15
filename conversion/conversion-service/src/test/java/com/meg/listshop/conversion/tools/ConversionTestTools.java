package com.meg.listshop.conversion.tools;

import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
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

    public static Unit makeUnit(Long id, UnitType type, UnitFlavor... flavors) {
        Unit unit = new Unit();
        unit.setType(type);
        unit.setId(id);
        for (UnitFlavor flavor : flavors) {
            switch (flavor) {
                case Weight:
                    unit.setWeight(true);
                    break;
                case Volume:
                    unit.setVolume(true);
                    break;
                case DishUnit:
                    unit.setDishUnit(true);
                    break;
                case Liquid:
                    unit.setLiquid(true);
                    break;
                case ListUnit:
                    unit.setLiquid(true);
            }


        }
        return unit;
    }

    public static Unit makeImperialUnit(Long id, UnitFlavor... flavors) {
        Unit unit = new Unit();
        unit.setType(UnitType.Imperial);
        unit.setId(id);
        for (UnitFlavor flavor : flavors) {
            switch (flavor) {
                case Weight:
                    unit.setWeight(true);
                    break;
                case Volume:
                    unit.setVolume(true);
                    break;
                case DishUnit:
                    unit.setDishUnit(true);
                    break;
                case Liquid:
                    unit.setLiquid(true);
                    break;
                case ListUnit:
                    unit.setLiquid(true);
            }


        }
        return unit;
    }

    public static Unit makeMetricUnit(Long id, UnitFlavor... flavors) {
        Unit unit = new Unit();
        unit.setType(UnitType.Metric);
        unit.setId(id);
        for (UnitFlavor flavor : flavors) {
            switch (flavor) {
                case Weight:
                    unit.setWeight(true);
                    break;
                case Volume:
                    unit.setVolume(true);
                    break;
                case DishUnit:
                    unit.setDishUnit(true);
                    break;
                case Liquid:
                    unit.setLiquid(true);
                    break;
                case ListUnit:
                    unit.setLiquid(true);
            }


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

    public static double roundToHundredths(double value) {
        return Math.round(value * 100D) / 100D;
    }
}