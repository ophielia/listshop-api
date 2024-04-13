package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.TagSpecificConversionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("tagSpecificHandler")
public class TagSpecificHandler extends AbstractConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TagSpecificHandler.class);

    @Autowired
    public TagSpecificHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing WeightVolumeHandler");

        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.METRIC, UnitSubtype.WEIGHT);

        ConversionFactorSource conversionSource = new TagSpecificConversionSource(factorRepository);

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
    }

}
