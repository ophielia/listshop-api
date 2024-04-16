package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionContext;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.conversion.service.factors.TagSpecificConversionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Qualifier("tagSpecificHandler")
public class TagSpecificHandler extends AbstractConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificHandler.class);

    private TagSpecificConversionSource conversionSource;

    @Autowired
    public TagSpecificHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing WeightVolumeHandler");

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

        List<ConversionFactor> factors = conversionSource.getAllPossibleFactors(toConvert, context.getConversionId());
        context.conversionFactorsFound(factors);
        return conversionSource.selectFactorsForConversion(toConvert, factors);

    }


}
