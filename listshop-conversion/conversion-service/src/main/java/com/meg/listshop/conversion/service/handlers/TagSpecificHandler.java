package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.TagSpecificConversionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@Qualifier("tagSpecificHandler")
public class TagSpecificHandler extends AbstractConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificHandler.class);

    @Value("${conversionservice.gram.unit.id:1013}")
    private Long GRAM_UNIT_ID;

    private TagSpecificConversionSource conversionSource;

    @Autowired
    public TagSpecificHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing TagSpecificHandler");

        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.METRIC, UnitSubtype.WEIGHT);

        conversionSource = new TagSpecificConversionSource(factorRepository);

        // initialize in abstract
        setSource(source);
        setTarget(target);

    }

    @Override
    public List<ConversionFactor> findFactors(ConvertibleAmount toConvert, ConversionContext context) {
        if (context.doesntRequireConversion(toConvert)) {
            LOG.debug("No conversion required for unitType: [{}], amount [{}].", context.getTargetUnitType(), toConvert);
            return null;
        }

        List<ConversionFactor> scalingFactors = conversionSource.getScalingFactors(toConvert, context);
        List<ConversionFactor> conversionFactors = pullConversionFactors(scalingFactors,  toConvert);
        context.conversionFactorsFound(scalingFactors);
        return conversionFactors;

    }

    private List<ConversionFactor> pullConversionFactors(List<ConversionFactor> scalingFactors, ConvertibleAmount toConvert) {
        return scalingFactors.stream()
                .filter(f -> f.getFromUnit().getId().equals(toConvert.getUnit().getId()))
                .collect(Collectors.toList());

    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount toConvert, ConversionContext context) throws ConversionFactorException {
        ConvertibleAmount amount = super.convert(toConvert, context);
        if (GRAM_UNIT_ID.equals(amount.getUnit().getId())) {
            context.setGramWeight(amount.getQuantity());
        }
        return amount;
    }

}
