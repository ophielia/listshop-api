package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.WeightVolumeConversionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("weightVolumeHandler")
public class WeightVolumeHandler extends AbstractConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WeightVolumeHandler.class);

    @Autowired
    public WeightVolumeHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing WeightVolumeHandler");

        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.HYBRID, UnitSubtype.SOLID);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.METRIC, UnitSubtype.WEIGHT);

        ConversionFactorSource conversionSource = new WeightVolumeConversionSource(factorRepository);

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
    }

}
