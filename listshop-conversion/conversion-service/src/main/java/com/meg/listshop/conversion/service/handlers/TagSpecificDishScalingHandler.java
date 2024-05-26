package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TagSpecificDishScalingHandler extends DishScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificDishScalingHandler.class);


    @Autowired
    public TagSpecificDishScalingHandler(ConversionFactorRepository factorRepository) {
        super(factorRepository);
        LOG.info("initializing TagSpecificDishScalingHandler");
    }


    @Override
    public boolean isTagSpecific() {
        return true;
    }

    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {
        List<ConversionFactor> factors = super.findFactors(toConvert, context);
        List<ConversionFactor> tagFactors = context.getTagSpecificFactors();
        UnitEntity unitToScale = toConvert.getUnit();
        double amountToScale = toConvert.getQuantity();
        double gramWeight = context.getGramWeight();

        double gramPerUnitFactor = gramWeight / Math.max(amountToScale, 0.0001);

        List<ConversionFactor> tagFactorsToAdd = new ArrayList<>();
        tagFactors.stream()
                .filter(f -> !Objects.equals(f.getFromUnit().getId(), f.getToUnit().getId()))
                .forEach(f -> {
                    double newFactor = gramPerUnitFactor / f.getFactor();
                    tagFactorsToAdd.add(SimpleConversionFactor.conversionFactor(unitToScale, f.getFromUnit(), newFactor));
                });

        if (!tagFactorsToAdd.isEmpty()) {
            factors.addAll(tagFactorsToAdd);
        }

        return factors;
    }
}

