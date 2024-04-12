package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.UnitSubtype;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ChainConversionHandler;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.service.handlers.ScalingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConverterServiceImpl implements ConverterService {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterServiceImpl.class);
    HashMap<HandlerChainKey, HandlerChain> chainMap = new HashMap<>();
    private final List<ChainConversionHandler> handlerList;
    private final List<ScalingHandler> scalerList;

    private final ConversionHandler weightVolumeHandler;

    @Autowired
    public ConverterServiceImpl(List<ChainConversionHandler> handlerList,
                                List<ScalingHandler> scalerList,
                                @Qualifier("weightVolumeHandler") ConversionHandler weightVolumeHandler) {
        this.handlerList = handlerList;
        this.scalerList = scalerList;
        this.weightVolumeHandler = weightVolumeHandler;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitType domain) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.specForDomain(amount.getUnit(), domain);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return doConversion(amount, target);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for context [{}], amount [{}]", context, amount);
        // get weight/volume target for context
        UnitSubtype targetSubtype = determineSubtypeFromContext(amount, context);
        ConversionSpec conversionSpec = ConversionSpec.specForContext(context.getUnitType(), targetSubtype, context.getContextType());

        return doConversion(amount, conversionSpec);
    }

    private UnitSubtype determineSubtypeFromContext(ConvertibleAmount toConvert, ConversionContext context) {
        // context dish, toConvert hybrid - return subtype of toConvert
        if (context.getContextType().equals(ConversionContextType.Dish) &&
                toConvert.getUnit().getType().equals(UnitType.HYBRID)) {
            return toConvert.getUnit().getSubtype();
        }
        if (context.getContextType().equals(ConversionContextType.Dish)) {
            // context dish, default is volume
            return UnitSubtype.VOLUME;
        }
        // context list, is liquid - return volume
        if (context.getContextType().equals(ConversionContextType.List) &&
                toConvert.getUnit().isLiquid()) {
            return UnitSubtype.VOLUME;
        }
        // context list, default is weight
        return UnitSubtype.WEIGHT;

    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for unit [{}], amount [{}]", targetUnit, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = createConversionSpec(targetUnit);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return doConversion(amount, target);
    }

    private ConvertibleAmount doConversion(ConvertibleAmount amount, ConversionSpec conversionSpec) throws ConversionPathException, ConversionFactorException {
        // need to come in here with a ConvertContext - unit available, (factors) , conversion context
        ConvertibleAmount result = amount;

        // if conversion necessary, convert for tag specific
        // required if - volume < = > weight
        //               tag specific available (conversion id not null)
        if (requiresAndCanDoTagSpecificConversion(amount, conversionSpec.getUnitSubtype())) {
            // weight / volume requirement requires metric type
            result = preConvertForWeightVolume(result, conversionSpec.getUnitSubtype());
            result = weightVolumeHandler.convert(result, conversionSpec);  // include context here
            // will return unit, if context is list (and unit is available)
            // if final unit (maybe in context? otherwise if unit = 'unit') then return
        }


        // continuing with result - is domain conversion necessary
        //  DomainHandler
        //       look for chain
        //       rework chain to be by domain only
        //       one handler for each domain (to metric) metric <=> us, metric <=> imperial
        //       two way handlers
        //       all units - volume / weight, etc.
        if (!result.getUnit().getType().equals(conversionSpec.getUnitType())) {
            result = convertDomain(result, conversionSpec.getUnitType(), conversionSpec.getUnitId());
        }
        // continuing with result - scaling
        //  ScalingHandler
        //       only used for List/Dish Context
        //       limits to unit types for context
        //       no cross domain conversions
        //       no weight to volume conversion
        ScalingHandler scalingHandler = getScalerForContext(conversionSpec.getContextType());
        if (scalingHandler != null) {
            return scalingHandler.scale(result);
        }

        return result;


    }

    private ScalingHandler getScalerForContext(ConversionContextType listOrDish) {
        if (listOrDish == null) {
            return null;
        }
        return scalerList.stream().filter(s -> s.scalerFor(listOrDish)).findFirst().orElse(null);
    }


    private ConvertibleAmount convertDomain(ConvertibleAmount amount, UnitType domainType, Long unitId) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domainType, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.basicSpec(unitId, domainType, null, new HashSet<>());

        if (source.getUnitType().equals(domainType)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return doDomainConversion(amount, source, target);

    }


    private ConvertibleAmount preConvertForWeightVolume(ConvertibleAmount amount, UnitSubtype targetSubtype) throws ConversionPathException, ConversionFactorException {
        if (targetSubtype.equals(UnitSubtype.VOLUME) && !amount.getUnit().getType().equals(UnitType.METRIC)) {
            return convert(amount, UnitType.METRIC);
        }
        return amount;
    }

    private boolean requiresAndCanDoTagSpecificConversion(ConvertibleAmount amount, UnitSubtype targetSubtype) {
        Set<UnitSubtype> subtypes = new HashSet<>();
        subtypes.add(amount.getUnit().getSubtype());
        subtypes.add(targetSubtype);

        if (subtypes.size() == 1) {
            // same subtype - no conversion necessary
            return false;
        }

        return subtypes.contains(UnitSubtype.SOLID);
    }

    private ConvertibleAmount doDomainConversion(ConvertibleAmount amount, ConversionSpec source, ConversionSpec target) throws ConversionFactorException, ConversionPathException {
        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);
    }

    private ConversionSpec createConversionSpec(UnitEntity unit) {
        return ConversionSpec.basicSpec(unit.getId(), unit.getType(), unit.getSubtype(), new HashSet<>());
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

    private boolean checkTargetEqualsSource(ConversionSpec source, ConversionSpec target) {
        return (source.equals(target));
    }
}
