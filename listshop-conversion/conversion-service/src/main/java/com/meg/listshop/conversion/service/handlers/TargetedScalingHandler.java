package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
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
public class TargetedScalingHandler extends AbstractScalingHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TargetedScalingHandler.class);


    @Autowired
    public TargetedScalingHandler(ConversionFactorRepository factorRepository) {
        super();
        setScalarWeight(4);
        LOG.info("initializing TargetedScalingHandler");
        // metric targets
        ConversionSpec metricSource = ConversionSpec.basicSpec(UnitType.METRIC, null);
        ConversionSpec metricTarget = ConversionSpec.basicSpec(UnitType.METRIC, null);
        // us targets
        ConversionSpec usSource = ConversionSpec.basicSpec(UnitType.US, null);
        ConversionSpec usTarget = ConversionSpec.basicSpec(UnitType.US, null);
        // hybrid targets
        ConversionSpec hybridSource = ConversionSpec.basicSpec(UnitType.HYBRID, null);
        ConversionSpec hybridTarget = ConversionSpec.basicSpec(UnitType.HYBRID, null);
        // imperial targets
        ConversionSpec imperialSource = ConversionSpec.basicSpec(UnitType.UK, null);
        ConversionSpec imperialTarget = ConversionSpec.basicSpec(UnitType.UK, null);

        // initialize conversionSource
        List<ConversionFactorEntity> factorEntities = factorRepository.findAll(where(matchingFromWithSpec(metricSource).and(matchingToWithSpec(metricTarget))));
        factorEntities.addAll(factorRepository.findAll(where(matchingFromWithSpec(usSource).and(matchingToWithSpec(usTarget)))));
        factorEntities.addAll(factorRepository.findAll(where(matchingFromWithSpec(hybridSource).and(matchingToWithSpec(hybridTarget)))));
        factorEntities.addAll(factorRepository.findAll(where(matchingFromWithSpec(imperialSource).and(matchingToWithSpec(imperialTarget)))));
        List<ConversionFactor> factors = factorEntities.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList());
        factors.addAll(selfScalingFactors(factors));
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors, true);

        // initialize in abstract
        setConversionSource(conversionSource);
        setScalerType(null);
        setSkipNoConversionRequiredCheck(true);
    }


}

