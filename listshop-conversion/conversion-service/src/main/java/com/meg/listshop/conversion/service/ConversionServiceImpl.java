package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.*;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
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
public class ConversionServiceImpl implements ConversionService {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionServiceImpl.class);

    HashMap<NewHandlerChainKey, NewHandlerChain> chainMap = new HashMap<>();
    private final List<ChainConversionHandler> handlerList;
    private final List<ScalingHandler> scalerList;

    private final ConversionHandler weightVolumeHandler;

    @Autowired
    public ConversionServiceImpl(List<ChainConversionHandler> handlerList,
            List<ScalingHandler> scalerList,
                                 @Qualifier("WeightVolumeHandler") ConversionHandler weightVolumeHandler) {
        this.handlerList = handlerList;
        this.scalerList = scalerList;
        this.weightVolumeHandler = weightVolumeHandler;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitType domain) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.specForDomain(amount.getUnit(), domain);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return interiorConvertTwo(amount,target);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for context [{}], amount [{}]", context, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());

        // get weight/volume target for context
        UnitSubtype targetSubtype = context.getContextType().equals(ConversionContextType.List) ? UnitSubtype.WEIGHT:
                UnitSubtype.VOLUME;
        ConversionSpec conversionSpec = ConversionSpec.basicSpec(context.getUnitType(), targetSubtype);

        if (checkTargetEqualsSource(source, conversionSpec)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, conversionSpec);
            return amount;
        }

        return interiorConvertTwo(amount, conversionSpec);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for unit [{}], amount [{}]", targetUnit, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = createConversionSpec(targetUnit);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return interiorConvertTwo(amount, target);
    }

    private ConvertibleAmount interiorConvertTwo(ConvertibleAmount amount,ConversionSpec conversionSpec) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        ConvertibleAmount result = amount;

        // if conversion necessary, convert between weight and volume
        if ( requiresAndCanDoWeightVolumeConversion(amount, conversionSpec.getUnitSubtype())) {
            //  WeightVolumeHandler
            //      handle on weighttovolume handler
            //      needs to go both ways
            //      remove notion of hybrid - cups, (old hybrid) to metric
            //      feeds into result amount

            // weight / volume requirement requires metric type
            result = preConvertForWeightVolume(result, conversionSpec.getUnitSubtype());
            result = weightVolumeHandler.convert(result, conversionSpec);
        }


        // continuing with result - is domain conversion necessary
        //  DomainHandler
        //       look for chain
        //       rework chain to be by domain only
        //       one handler for each domain (to metric) metric <=> us, metric <=> imperial
        //       two way handlers
        //       all units - volume / weight, etc.
        if (!result.getUnit().getType().equals(conversionSpec.getUnitType())) {
            result = convertDomain(result, conversionSpec.getUnitType());
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
        return scalerList.stream().filter( s -> s.  scalerFor(listOrDish)).findFirst().orElse(null);
    }


    private ConvertibleAmount convertDomain(ConvertibleAmount amount, UnitType domainType) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domainType, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.basicSpec(domainType, null);

        if (source.getUnitType().equals(domainType)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return doDomainConversion(amount, source, target);

    }


    private ConvertibleAmount preConvertForWeightVolume(ConvertibleAmount amount, UnitSubtype targetSubtype) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        if (targetSubtype.equals(UnitSubtype.VOLUME) && !amount.getUnit().getType().equals(UnitType.METRIC)) {
        return convert(amount, UnitType.METRIC);
        }
        return amount;
    }

    private boolean requiresAndCanDoWeightVolumeConversion(ConvertibleAmount amount, UnitSubtype targetSubtype) {
        Set<UnitSubtype> subtypes = new HashSet<>();
        subtypes.add(amount.getUnit().getSubtype());
        subtypes.add(targetSubtype);

        if (subtypes.size() == 1) {
            // same subtype - no conversion necessary
            return false;
        }

        if (subtypes.contains(UnitSubtype.SOLID) &&
              subtypes.contains(UnitSubtype.VOLUME)) {
            return true;
        }

        return false;
    }



    private ConvertibleAmount doDomainConversion(ConvertibleAmount amount, ConversionSpec source, ConversionSpec target) throws ConversionFactorException, ExceedsAllowedScaleException, ConversionPathException {
        // find or create handler chain for source / target
        NewHandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);
    }

    private ConversionSpec createConversionSpec(UnitEntity unit) {
        return ConversionSpec.basicSpec(unit.getId(), unit.getType(), unit.getSubtype(), new HashSet<>());
    }

    private NewHandlerChain getOrCreateChain(ConversionSpec source, ConversionSpec target) throws ConversionPathException {
        NewHandlerChainKey conversionKey = new NewHandlerChainKey(source, target);

        if (chainMap.containsKey(conversionKey)) {
            LOG.trace("Found existing chain for key: [{}]", conversionKey);
            return chainMap.get(conversionKey);
        }

        NewHandlerChain newChain = createConversionChain(source, target);
        chainMap.put(conversionKey, newChain);
        return newChain;
    }

    private NewHandlerChain createConversionChain(ConversionSpec sourceSpec, ConversionSpec targetSpec) throws ConversionPathException {
        LOG.info("Creating chain for source: [{}], target [{}]", sourceSpec, targetSpec);
        // assemble handler chain list
        List<ChainConversionHandler> handlers = assembleHandlerList(sourceSpec, targetSpec, new ArrayList<>(), 0);

        // convert list into handler chain
        if ( handlers.isEmpty()) {
            String message = String.format("No handler chain can be assembled for source: %s target: %s", sourceSpec, targetSpec);
            LOG.warn(message);
            throw new ConversionPathException(message);
        } else if (handlers.size() == 1) {
            return new NewHandlerChain(handlers.get(0));
        }

        // we have more than one handler - we'll make a handler chain
        return assembleHandlerChain(new NewHandlerChain(handlers.get(handlers.size() - 1)),
                handlers,
                handlers.size() - 2);
    }

    private NewHandlerChain assembleHandlerChain(NewHandlerChain handlerChain, List<ChainConversionHandler> handlers, int i) {
        if (i < 0) {
            return handlerChain;
        }
        NewHandlerChain linkToBefore = new NewHandlerChain(handlers.get(i));
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
        if (iteration > handlerList.size()) {
            String message = String.format("No handler chain can be assembled for fromUnit: %s toUnit: %s", source, target);
            throw new ConversionPathException(message);
        }

        // look for step matches
        for (ChainConversionHandler handler : handlerList) {
            if (handler.convertsTo(target)) {
                List<ChainConversionHandler> foundList = assembleHandlerList(source, handler.getSource(), handlers, iteration + 1);
                    if ( !foundList.isEmpty()) {
                        foundList.add(handler);
                        return foundList;
                }

            }
        }
        return new ArrayList<>();
    }



    private ChainConversionHandler findHandlerMatch(ConversionSpec source, ConversionSpec target) {
        return handlerList.stream()
                .filter(h -> h.handlesDomain(source, target))
                .findFirst().orElse(null);
    }

    private boolean checkTargetEqualsSource(ConversionSpec source, ConversionSpec target) {
        return (source.equals(target));
    }
}
