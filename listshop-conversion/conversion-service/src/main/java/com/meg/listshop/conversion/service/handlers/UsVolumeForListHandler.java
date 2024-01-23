package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.SimpleConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.matchingFromWithSpec;
import static com.meg.listshop.conversion.data.repository.UnitSpecifications.matchingToWithSpec;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class UsVolumeForListHandler extends AbstractOneWayConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UsVolumeForListHandler.class);


    @Autowired
    public UsVolumeForListHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing UsVolumeForListHandler");
        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.US, UnitSubtype.VOLUME);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.US, UnitSubtype.VOLUME, UnitFlavor.ListUnit);

        // initialize conversionSource
        List<ConversionFactorEntity> factorEntities = factorRepository.findAll(where(matchingFromWithSpec(source).and(matchingToWithSpec(target))));
        List<ConversionFactor> factors = factorEntities.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList());
        factors.addAll(selfScalingFactors(factors));
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors, true);

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
        setRestrictRange();
        setDoesScaling(true);
    }

}

