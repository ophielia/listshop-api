/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.service;


import com.meg.listshop.common.StringTools;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.processors.ConverterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

//import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;


@Service("converterService")
@Primary
public class ConverterServiceImpl implements ConverterService {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterServiceImpl.class);
    private final List<ConverterProcessor> processors;


    @Autowired
    public ConverterServiceImpl(List<ConverterProcessor> processors) {
        this.processors = processors;
    }


    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, DomainType domain) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);

        // create context
        UnitType unitDomainType = domainToUnitType(domain);
        ConversionTarget target = new ConversionTarget(unitDomainType, null, null);
        ProcessingContext context = new ProcessingContext(amount, target);// feed to converter chain
        return doConversion(context);
    }

    private ConvertibleAmount doConversion(ProcessingContext context) {
        for (ConverterProcessor processor : processors) {
            if (processor.appliesTo(context)) {
                processor.process(context);
            }
        }
        return context.getCurrentAmount();
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionRequest conversionRequest) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for context [{}], amount [{}, unitSize [{}]", conversionRequest, amount, conversionRequest.getUnitSize());

        if (conversionRequest == null) {
            throw new ConversionPathException("Cannot convert, context is null");
        }

        UnitType unitDomainType = domainToUnitType(conversionRequest.getDomainType());
        ConversionTargetType conversionTarget = conversionRequest.getContextType();

        ConversionTarget target = new ConversionTarget(unitDomainType, null, conversionTarget);
        ProcessingContext context = new ProcessingContext(amount, target);

        return doConversion(context);
    }


    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit) throws ConversionPathException, ConversionFactorException {
        return convert(amount, targetUnit, null);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException {
        //MM conversion - come back for size
        LOG.debug("Beginning convert for unit [{}], amount [{}]", targetUnit, amount);

        // create context
        UnitType unitDomainType = targetUnit.getType();
        ConversionTarget target = new ConversionTarget(unitDomainType, targetUnit.getId(), null, unitSize, null);
        ProcessingContext context = new ProcessingContext(amount, target, targetUnit);
        doConversion(context);

        return context.getCurrentAmount();
    }

    public ConvertibleAmount add(ConvertibleAmount amountToAdd, ConvertibleAmount addTo, AddScaleRequest request) throws ConversionPathException, ConversionFactorException, ConversionAddException {
        // check if the units are the same
        if (amountToAdd.getUnit() == null || addTo.getUnit() == null) {
            String message = String.format("Cannot add unit, from [%s] or to unit [%s] is null", amountToAdd.getUnit(), addTo.getUnit());
            throw new ConversionAddException(message);
        }
        if (!amountToAdd.getUnit().getId().equals(addTo.getUnit().getId())) {
            String message = String.format("Cannot add unit, units are not the same: from unit [%s], to unit [%s] ", amountToAdd.getUnit().getId(), addTo.getUnit().getId());
            throw new ConversionAddException(message);
        }
        // check if the markers are the same
        if (!StringTools.stringIsEmpty(amountToAdd.getMarker()) &&
                !StringTools.stringIsEmpty(addTo.getMarker()) &&
                !amountToAdd.getMarker().equals(addTo.getMarker())) {
            //      otherwise error - this would be an edge case, since this will primarily be used in the context
            //      of adding a dish ingredient to a list - so the units should be list units, which typically don't have
            //      markers
            String message = String.format("Cannot add unit, unit markers are not the same: from unit [%s], to unit [%s] ", amountToAdd.getMarker(), addTo.getMarker());
            throw new ConversionAddException(message);
        }


        // check if the sizes are the same
        // we use the addTo size if it's user entered
        // otherwise we use the addFrom size, if it's user entered
        String targetUnitSize = determineUnitSizePrecedenceForAdd(amountToAdd, addTo);
        ConversionTarget target = new ConversionTarget(request.getUnitType(), addTo.getUnit().getId(),
                request.getContextType(), targetUnitSize, null);
        ProcessingContext baseContext = new ProcessingContext(null, target, null);

        amountToAdd = equalizeSize(amountToAdd, addTo, baseContext);
        addTo = equalizeSize(addTo, amountToAdd, baseContext);

        double quantity = addTo.getQuantity();
        quantity += amountToAdd.getQuantity();
        boolean userSize = addTo.getUserSize() || amountToAdd.getUserSize();
        String summedUnitSize = addTo.getUnitSize();
        ConvertibleAmount summedAmount = new SimpleAmount(quantity,
                addTo.getUnit(),
                addTo.getConversionId(),
                addTo.getIsLiquid(),
                addTo.getMarker(),
                summedUnitSize,
                userSize);

        if (baseContext.getTarget().domainType().equals(UnitType.UNIT)) {
            // scaling for units already done in equalizing sizes
            return summedAmount;
        }

        // do scaling
        ConversionTarget scalingTarget = new ConversionTarget(request.getUnitType(), null,
                request.getContextType(), targetUnitSize, null);
        ProcessingContext scalingContext = new ProcessingContext(summedAmount, scalingTarget, null);
        summedAmount = doConversion(scalingContext);

        // return result
        return summedAmount;

    }


    private ConvertibleAmount equalizeSize(ConvertibleAmount amountToAdd, ConvertibleAmount addTo, ProcessingContext baseContext) {
        String targetedSize = baseContext.getTarget().unitSize();
        if (!sizesMatch(amountToAdd.getUnitSize(), targetedSize)) {
            ProcessingContext equalizeContext = new ProcessingContext(amountToAdd, baseContext.getTarget(), addTo.getUnit());
            return doConversion(equalizeContext);
        }
        return amountToAdd;
    }

    private boolean sizesMatch(String unitSize, String targetedSize) {
        if (unitSize == null && targetedSize == null) {
            return true;
        }
        if (unitSize == null || targetedSize == null) {
            return false;
        }
        return (unitSize.equals(targetedSize));
    }

    public ConvertibleAmount scale(ConvertibleAmount toScale, AddScaleRequest request) throws ConversionFactorException {
        // do the scaling
        /* create context
        ConversionSpec spec = ConversionSpec.specForAddRequest(request);
        ConversionContext context = new ConversionContext(toScale, spec);
        prepareContextForTagSpecificScaling(context);
        ScalingHandler scalingHandler = getScalerForContext(context);

        // do scaling
        if (scalingHandler != null && !context.isUnitToUnit()) {
            return scalingHandler.scale(toScale, context);
        }

        // return result
        return toScale;
*/
        ConversionTarget target = new ConversionTarget(request.getUnitType(), null, request.getContextType(), request.getUnitSize(), toScale.getMarker());
        ProcessingContext pContext = new ProcessingContext(toScale, target);
        return doConversion(pContext);
        // new ConversionSpec(null, request.getUnitType(), request.getSubtype(),
        //       request.getContextType(), request.getUnitSize(), null, null);
    }

    private String determineUnitSizePrecedenceForAdd(ConvertibleAmount amountToAdd, ConvertibleAmount addTo) {
        boolean toAddIsUserEntered = amountToAdd.getUserSize() != null && amountToAdd.getUserSize();
        boolean addToIsUserEntered = addTo.getUserSize() != null && addTo.getUserSize();

        if (addToIsUserEntered) {
            return addTo.getUnitSize();
        }
        if (toAddIsUserEntered) {
            return amountToAdd.getUnitSize();
        }
        return addTo.getUnitSize();
    }

    private UnitType domainToUnitType(DomainType domain) {
        return switch (domain) {
            case US -> UnitType.US;
            case METRIC -> UnitType.METRIC;
            case UK -> UnitType.UK;
            case ALL -> UnitType.ALL;
        };
    }

}
