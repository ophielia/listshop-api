package com.meg.listshop.conversion.service.handlers;

import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
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
public class VolumeHandler extends AbstractConversionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(VolumeHandler.class);


    @Autowired
    public VolumeHandler(ConversionFactorRepository factorRepository) {
        super();
        // make source from unit
        ConversionSpec source = ConversionSpec.basicSpec(UnitType.Metric, UnitFlavor.Volume);
        // make target
        ConversionSpec target = ConversionSpec.basicSpec(UnitType.Imperial, UnitFlavor.Volume);

        // initialize conversionSource
        List<ConversionFactorEntity> factors = factorRepository.findAll(where(matchingFromWithSpec(source).and(matchingToWithSpec(target))));
        ConversionFactorSource conversionSource = new SimpleConversionFactorSource(factors.stream().map(f -> (ConversionFactor) f).collect(Collectors.toList()));

        // initialize in abstract
        setSource(source);
        setTarget(target);
        setConversionSource(conversionSource);
    }

}

