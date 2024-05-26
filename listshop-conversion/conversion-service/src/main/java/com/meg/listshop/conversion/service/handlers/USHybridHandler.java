package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.conversion.data.repository.ConversionFactorRepository;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.ConversionFactorSource;
import com.meg.listshop.conversion.service.factors.SimpleConversionFactorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meg.listshop.conversion.data.repository.UnitSpecifications.matchingFromWithSpec;
import static com.meg.listshop.conversion.data.repository.UnitSpecifications.matchingToWithSpec;
import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class USHybridHandler extends AbstractChainConversionHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(USHybridHandler.class);


    @Autowired
    public USHybridHandler(ConversionFactorRepository factorRepository) {
        super();
        LOG.info("initializing USToHybridHandler");
        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.HYBRID,null);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.US, null);

        // initialize conversionSource
        Set<ConversionFactorEntity> factors = new HashSet<>(factorRepository.findAll(where(matchingFromWithSpec(source).and(matchingToWithSpec(target)))));
        factors.addAll(factorRepository.findAll(where(matchingFromWithSpec(target).and(matchingToWithSpec(source)))));
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList()));

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
    }

}

