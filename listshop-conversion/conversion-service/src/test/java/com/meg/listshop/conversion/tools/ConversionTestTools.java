package com.meg.listshop.conversion.tools;

import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;

public class ConversionTestTools {
    public static UnitEntity makeImperialUnit(Long id, boolean isVolume) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.Imperial);
        unit.setId(id);
        if (isVolume) {
            unit.setVolume(true);
        } else {
            unit.setWeight(true);
        }
        return unit;
    }

    public static UnitEntity makeUnit(Long id, UnitType type, UnitSubtype subtype, UnitFlavor... flavors) {
        UnitEntity unit = new UnitEntity();
        unit.setType(type);
        unit.setSubtype(subtype);
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
                    unit.setListUnit(true);
            }


        }
        return unit;
    }

    public static UnitEntity makeImperialUnit(Long id, UnitSubtype subtype, UnitFlavor... flavors) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.Imperial);
        unit.setSubtype(subtype);
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
                    unit.setListUnit(true);
            }


        }
        return unit;
    }

    public static UnitEntity makeMetricUnit(Long id, UnitSubtype subtype, UnitFlavor... flavors) {
        UnitEntity unit = new UnitEntity();
        unit.setType(UnitType.Metric);
        unit.setSubtype(subtype);
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

    public static UnitEntity makeMetricUnit(Long id, boolean isVolume) {
        UnitEntity unit = new UnitEntity();
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