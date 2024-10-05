package com.meg.listshop.conversion.service;


import com.meg.listshop.common.RoundingUtils;
import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.ScalingHandler;
import com.meg.listshop.conversion.service.handlers.UnitScalingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class ConverterServiceImpl implements ConverterService {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterServiceImpl.class);
    HashMap<HandlerChainKey, HandlerChain> chainMap = new HashMap<>();
    private final List<ChainConversionHandler> handlerList;
    private final List<ScalingHandler> scalerList;

    private final ConversionHandler tagSpecificHandler;


    @Autowired
    public ConverterServiceImpl(List<ChainConversionHandler> handlerList,
                                List<ScalingHandler> scalerList,
                                @Qualifier("tagSpecificHandler") ConversionHandler weightVolumeHandler) {
        this.handlerList = handlerList;
        this.scalerList = scalerList;
        this.tagSpecificHandler = weightVolumeHandler;
    }


    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, DomainType domain) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);
        if (domain == null) {
            throw new ConversionPathException("Cannot convert, domain is null");
        }
        ConversionSpec target = ConversionSpec.specForDomain(amount.getUnit(), domain);

        return doConversion(amount, target);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionRequest conversionRequest) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for context [{}], amount [{}, unitSize [{}]", conversionRequest, amount, conversionRequest.getUnitSize());
        if (conversionRequest == null) {
            throw new ConversionPathException("Cannot convert, context is null");
        }
        UnitSubtype targetSubtype = determineSubtypeFromContext(amount, conversionRequest);
        ConversionSpec conversionSpec = ConversionSpec.specForConversionRequest(conversionRequest, targetSubtype);

        return doConversion(amount, conversionSpec);
    }


    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit) throws ConversionPathException, ConversionFactorException {
        return convert(amount, targetUnit, null);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit, String unitSize) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for unit [{}], amount [{}]", targetUnit, amount);
        if (targetUnit == null) {
            throw new ConversionPathException("Target unit is null");
        }
        ConversionSpec target = createConversionSpec(targetUnit, unitSize);

        return doConversion(amount, target);
    }

    public ConvertibleAmount add(ConvertibleAmount amountToAdd, ConvertibleAmount addTo, AddRequest request) throws ConversionPathException, ConversionFactorException {
        // create context
        ConversionSpec spec = ConversionSpec.specForAddRequest( request);
        ConversionContext context = new ConversionContext(amountToAdd, spec);

        // check if the units are the same
        //      otherwise throw error
        // check if the sizes are the same
        //      otherwise convert amountToAdd size to addTo size
        // check if the markers are the same
        //      otherwise error - this would be an edge case, since this will primarily be used in the context
        //      of adding a dish ingredient to a list - so the units should be list units, which typically don't have
        //      markers

        // do the adding
        double quantity = addTo.getQuantity();
        quantity += amountToAdd.getQuantity();
        ConvertibleAmount summedAmount = new SimpleAmount(quantity,
                addTo.getUnit(),
                addTo.getConversionId(),
                addTo.getIsLiquid(),
                addTo.getMarker(),
                addTo.getUnitSize());

        // do scaling
        ScalingHandler scalingHandler = getScalerForContext(context);
        if (scalingHandler != null) {
            return scalingHandler.scale(summedAmount, context);
        }

        // return result
        return summedAmount;
    }

    private UnitSubtype determineSubtypeFromContext(ConvertibleAmount toConvert, ConversionRequest context) {
        // context dish, toConvert hybrid - return subtype of toConvert
        if (context.getContextType().equals(ConversionTargetType.Dish) &&
                toConvert.getUnit().getType().equals(UnitType.HYBRID)) {
            return toConvert.getUnit().getSubtype();
        }
        if (context.getContextType().equals(ConversionTargetType.Dish)) {
            // context dish, default is volume
            return UnitSubtype.VOLUME;
        }
        // context list, is liquid - return volume
        if (context.getContextType().equals(ConversionTargetType.List) &&
                toConvert.getUnit().isLiquid()) {
            return UnitSubtype.VOLUME;
        }
        // context list, default is weight
        return UnitSubtype.WEIGHT;

    }


    private ConvertibleAmount doConversion(ConvertibleAmount amount, ConversionSpec conversionSpec) throws ConversionPathException, ConversionFactorException {
        ConvertibleAmount result = amount;

        // return if no conversion necessary
        if (noConversionNecessary(amount, conversionSpec)) {
            LOG.info("No conversion to do for source [{}] and target [{}]. ", amount, conversionSpec);
            return amount;
        }

        // Create ConversionContext
        ConversionContext context = new ConversionContext(amount, conversionSpec);

        // if conversion necessary, convert for tag specific
        // required if - volume < = > weight
        //               tag specific available (conversion id not null)
        if (context.requiresAndCanDoTagSpecificConversion(amount)) {
            // weight / volume requirement requires metric type
            result = preConvertForWeightVolume(result, context);
            result = tagSpecificHandler.convert(result, context);
        }


        // continuing with result - is domain conversion necessary
        //  DomainHandler
        //       look for chain
        //       rework chain to be by domain only
        //       one handler for each domain (to metric) metric <=> us, metric <=> imperial
        //       two way handlers
        //       all units - volume / weight, etc.
        if (context.requiresDomainConversion(result)) { //!result.getUnit().getType().equals(conversionSpec.getUnitType())
            result = convertDomain(result, context); // conversionSpec.getUnitType(), conversionSpec.getUnitId()
        }
        // continuing with result - scaling
        //  ScalingHandler
        //       only used for List/Dish Context
        //       limits to unit types for context
        //       no cross domain conversions
        //       no weight to volume conversion
        ScalingHandler scalingHandler = getScalerForContext(context);
        if (scalingHandler != null) {
            return scalingHandler.scale(result, context);
        }

        return result;


    }

    private boolean noConversionNecessary(ConvertibleAmount amount, ConversionSpec conversionSpec) {
        // no conversion necessary if
        // amount unit id = spec unit id
        if (conversionSpec.getUnitId() != null && amount.getUnit().getId().equals(conversionSpec.getUnitId())) {
            return true;
        }
        if (conversionSpec.getContextType() != null && conversionSpec.getContextType().equals(ConversionTargetType.List)) {
            return domainMatches(amount, conversionSpec) && amount.getUnit().isListUnit();
        }
        if (conversionSpec.getContextType() != null && conversionSpec.getContextType().equals(ConversionTargetType.Dish)) {
            return domainMatches(amount, conversionSpec) && amount.getUnit().isDishUnit();
        }
        return false;
    }

    private boolean domainMatches(ConvertibleAmount amount, ConversionSpec conversionSpec) {
        // unit matches everything
        if (amount.getUnit().getType().equals(UnitType.UNIT)) {
            return true;
        }
        return conversionSpec.getUnitType() != null &&
                conversionSpec.getUnitType().equals(amount.getUnit().getType());
    }

    private ScalingHandler getScalerForContext(ConversionContext context) {
        if (context.shouldScaleToUnit()) {
            return new UnitScalingHandler();
        }
        return scalerList.stream().filter(s -> s.scalerFor(context)).findFirst().orElse(null);
    }

    private ConvertibleAmount convertDomain(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException {
        UnitType domainType = context.getTargetUnitType();
        UnitType sourceType = amount.getUnit().getType();
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domainType, amount);

        if (sourceType.equals(domainType)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", sourceType, domainType);
            return amount;
        }
        return doDomainConversion(amount, context);

    }


    private ConvertibleAmount preConvertForWeightVolume(ConvertibleAmount amount, ConversionContext context)
            throws ConversionPathException, ConversionFactorException {
        if (context.getTargetSubtype().equals(UnitSubtype.VOLUME)
                && !amount.getUnit().getType().equals(UnitType.METRIC)) {
            return convert(amount, DomainType.METRIC);
        }
        return amount;
    }


    private ConvertibleAmount doDomainConversion(ConvertibleAmount amount, ConversionContext context) throws ConversionFactorException, ConversionPathException {
        Long unitId = context.getTargetUnitId();
        UnitType domainType = context.getTargetUnitType();
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.basicSpec(unitId, domainType, null, new HashSet<>());

        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, context);
    }

    private ConversionSpec createConversionSpec(UnitEntity unit) {
        return ConversionSpec.basicSpec(unit.getId(), unit.getType(), unit.getSubtype(), new HashSet<>());
    }

    private ConversionSpec createConversionSpec(UnitEntity unit, String unitSize) {
        return ConversionSpec.basicSpec(unit.getId(), unit.getType(), unit.getSubtype(), unitSize, new HashSet<>());
    }

    private HandlerChain getOrCreateChain(ConversionSpec source, ConversionSpec target) throws ConversionPathException {
        HandlerChainKey conversionKey = new HandlerChainKey(source, target);

        if (chainMap.containsKey(conversionKey)) {
            LOG.trace("Found existing chain for key: [{}]", conversionKey);
            return chainMap.get(conversionKey);
        }

        HandlerChain newChain = createConversionChain(source, target);
        chainMap.put(conversionKey, newChain);
        return newChain;
    }

    private HandlerChain createConversionChain(ConversionSpec sourceSpec, ConversionSpec targetSpec) throws ConversionPathException {
        LOG.info("Creating chain for source: [{}], target [{}]", sourceSpec, targetSpec);
        // assemble handler chain list
        List<ChainConversionHandler> handlers = assembleHandlerList(sourceSpec, targetSpec, new ArrayList<>(), 0);

        // convert list into handler chain
        if (handlers.isEmpty()) {
            String message = String.format("No handler chain can be assembled for source: %s target: %s", sourceSpec, targetSpec);
            LOG.warn(message);
            throw new ConversionPathException(message);
        } else if (handlers.size() == 1) {
            return new HandlerChain(handlers.get(0));
        }

        // we have more than one handler - we'll make a handler chain
        return assembleHandlerChain(new HandlerChain(handlers.get(handlers.size() - 1)),
                handlers,
                handlers.size() - 2);
    }

    private HandlerChain assembleHandlerChain(HandlerChain handlerChain, List<ChainConversionHandler> handlers, int i) {
        if (i < 0) {
            return handlerChain;
        }
        HandlerChain linkToBefore = new HandlerChain(handlers.get(i));
        linkToBefore.setNextLink(handlerChain);
        return assembleHandlerChain(linkToBefore, handlers, i - 1);
    }

    private List<ChainConversionHandler> assembleHandlerList(ConversionSpec source, ConversionSpec target, List<ChainConversionHandler> handlers, int iteration) throws ConversionPathException {
        // look for direct match
        ChainConversionHandler directMatch = findHandlerMatch(source, target);
        if (directMatch != null) {
            handlers.add(0, directMatch);
            return handlers;
        }
        // check for too many iterations
        if (iteration > 10) {
            String message = String.format("No handler chain can be assembled for fromUnit: %s toUnit: %s", source, target);
            throw new ConversionPathException(message);
        }

        // look for step matches
        for (ChainConversionHandler handler : handlerList) {
            if (handler.convertsToDomain(target.getUnitType())) {
                List<ChainConversionHandler> foundList = assembleHandlerList(source, handler.getOppositeSource(target.getUnitType()), handlers, iteration + 1);
                if (!foundList.isEmpty()) {
                    foundList.add(handler);
                    return foundList;
                }

            }
        }
        return new ArrayList<>();
    }


    private ChainConversionHandler findHandlerMatch(ConversionSpec source, ConversionSpec target) {
        return handlerList.stream()
                .filter(h -> h.handlesDomain(source.getUnitType(), target.getUnitType()))
                .findFirst().orElse(null);
    }


}
