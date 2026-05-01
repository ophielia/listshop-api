/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.DeltaSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.SimpleConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewMetricUkHandler extends BaseDomainConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NewMetricUkHandler.class);

    @Autowired
    public NewMetricUkHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing MetricUkHandler");
        // make source from unit
        DeltaSpec source = new DeltaSpec(UnitType.METRIC, null, null);

        // make target
        DeltaSpec target = new DeltaSpec(UnitType.UK, null, null);

        // initialize conversionSource
        List<ConversionFactorEntity> factors = factorRepository.findAllByDomains(DomainType.METRIC.name(), DomainType.UK.name());
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList()));

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
    }

}

