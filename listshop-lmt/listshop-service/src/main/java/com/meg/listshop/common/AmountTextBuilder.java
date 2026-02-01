package com.meg.listshop.common;

import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.lmt.conversion.QuantityElements;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;

public class AmountTextBuilder {
    private double quantity;
    private UnitEntity unit;
    private String marker;
    private String size;
    private boolean ignoreUnit = false;
    private QuantityElements elements;
    private boolean noAmount = false;

    private static final Long UNIT_UNIT_ID = 1011L;

    public AmountTextBuilder with(ListItemEntity item) {
        if (item.getUnit() == null || item.getRawQuantity() == 0) {
            noAmount = true;
            return this;
        }

        quantity = CommonUtils.elvis(item.getRoundedQuantity(), 0.0);
        size = item.getUnitSize();
        this.unit = item.getUnit();
        this.ignoreUnit = (this.unit == null || this.unit.getId().equals(UNIT_UNIT_ID));
        this.size = item.getUnitSize();

        return this;
    }

    public AmountTextBuilder with(ListItemDetailEntity detail) {
        double calculationQuantity;
        if (detail.getUnitId() == null) {
            noAmount = true;
            return this;
        }
        if (detail.getUnitId().equals(UNIT_UNIT_ID)) {
            calculationQuantity = RoundingUtils.roundUpToNearestWholeNumber(detail.getQuantity());
        } else {
            calculationQuantity = RoundingUtils.roundUpToNearestFraction(detail.getQuantity());
        }
        QuantityElements quantityElements = FractionUtils.splitQuantityIntoElements(calculationQuantity);
        quantity = quantityElements.quantity();
        size = detail.getUnitSize();
        this.size = detail.getUnitSize();
        this.marker = detail.getMarker();
        return this;
    }

    public AmountTextBuilder withElements(QuantityElements elements) {
        this.elements = elements;
        return this;
    }

    public AmountTextBuilder with(UnitEntity unit) {
        this.unit = unit;
        this.ignoreUnit = (unit == null || unit.getId().equals(UNIT_UNIT_ID));
        return this;
    }


    public String build() {
        if (noAmount || quantity == 0) {
            return "";
        }
        if (this.elements == null) {
            elements = FractionUtils.splitQuantityIntoElements(quantity);
        }
        StringBuilder builder = new StringBuilder();
        if (elements.wholeNumber() > 0) {
            builder.append(elements.wholeNumber()).append(" ");
        }
        if (elements.fractionType() != null) {
            builder.append(elements.fractionType().getDisplayName()).append(" ");
        }
        if (this.unit != null && !this.ignoreUnit) {
            builder.append(unit.getName()).append(" ");
        }
        if (this.size != null) {
            builder.append(size).append(" ");
        }
        if (this.marker != null) {
            builder.append(marker).append(" ");
        }
        String text = builder.toString();
        return text.trim();
    }

}
