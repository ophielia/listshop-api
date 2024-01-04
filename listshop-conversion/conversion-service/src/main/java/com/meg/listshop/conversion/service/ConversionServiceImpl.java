package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import com.meg.listshop.conversion.tools.ConversionTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversionServiceImpl implements ConversionService {
    private static final Logger LOG = LoggerFactory.getLogger(ConversionServiceImpl.class);

    HashMap<HandlerChainKey, HandlerChain> chainMap = new HashMap<>();
    private final List<ConversionHandler> handlerList;

    @Autowired
    public ConversionServiceImpl(List<ConversionHandler> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitType domain) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);
        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.opposingSpec(source, domain);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        return doConvert(amount, source, target);

    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException, ExceedsAllowedScaleException {
        LOG.debug("Beginning convert for context [{}], amount [{}]", context, amount);

        ConversionSpec source = createConversionSpec(amount.getUnit());
        ConversionSpec target = ConversionSpec.fromContext(context, amount.getUnit());

        try {
            return doConvert(amount,source, target);
        } catch (ExceedsAllowedScaleException e) {
            // the scale was incorrect for this conversion
            // this happens with hybrids - for example - 16 tablespoons
            // which convert to a cup, which isn't a hybrid unit
            LOG.info("Scaled out of hybrid unit for source [{}]", amount.getUnit());
        }

        // create alternate target
        ConversionSpec alternateTarget = ConversionSpec.retryFromContext(context, amount.getUnit());
        return doConvert(amount, source, alternateTarget);
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

        return doConvert(amount,source,target);
    }

    private ConvertibleAmount doConvert(ConvertibleAmount amount, ConversionSpec source, ConversionSpec target) throws ConversionFactorException, ExceedsAllowedScaleException, ConversionPathException {
        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);
    }

    private ConversionSpec createConversionSpec(UnitEntity unit) {
        Set<UnitFlavor> specFlavors = ConversionTools.flavorsForUnit(unit);

        specFlavors = specFlavors.stream()
                    .filter(f -> f != UnitFlavor.ListUnit)
                    .filter(f -> f != UnitFlavor.DishUnit)
                    .collect(Collectors.toSet());

        return ConversionSpec.basicSpec(unit.getId(), unit.getType(), unit.getSubtype(), specFlavors);
    }

    private HandlerChain getOrCreateChain(ConversionSpec source, ConversionSpec target) throws ConversionPathException {
        HandlerChainKey conversionKey = new HandlerChainKey(source, target);

        if (chainMap.containsKey(conversionKey)) {
            LOG.trace("Found existing chain for key: [{}]", conversionKey);
            return chainMap.get(conversionKey);
        }

        for (ConversionHandler handler : handlerList) {
            LOG.info("handler handles: [{}]", handler.getClass().getName());
            LOG.info("...: source [{}]", handler.getAllSources());
            LOG.info("...: target [{}]", handler.getTarget());

        }

        HandlerChain newChain = createConversionChain(source, target);
        chainMap.put(conversionKey, newChain);
        return newChain;
    }

    private HandlerChain createConversionChain(ConversionSpec sourceSpec, ConversionSpec targetSpec) throws ConversionPathException {
        LOG.info("Creating chain for source: [{}], target [{}]", sourceSpec, targetSpec);
        // assemble handler chain list
        Set<HandlerChainKey> checkedPairs = new HashSet<>();
        List<ConversionHandler> handlers = assembleHandlerList(sourceSpec, targetSpec, new ArrayList<>(), 0, checkedPairs);

        // convert list into handler chain
        if ( handlers.isEmpty()) {
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

    private HandlerChain assembleHandlerChain(HandlerChain handlerChain, List<ConversionHandler> handlers, int i) {
        if (i < 0) {
            return handlerChain;
        }
        HandlerChain linkToBefore = new HandlerChain(handlers.get(i));
        linkToBefore.setNextLink(handlerChain);
        return assembleHandlerChain(linkToBefore, handlers, i - 1);
    }


    private List<ConversionHandler> assembleHandlerList(ConversionSpec source, ConversionSpec target, List<ConversionHandler> handlers, int iteration, Set<HandlerChainKey> checkedPairs) throws ConversionPathException {
        LOG.info(". iteration: [{}]", iteration);
        LOG.info(".... checking source: [{}]", source);
        LOG.info(".... checking target: [{}]", target);
        // look for direct match
        ConversionHandler directMatch = findHandlerMatch(source, target);
        if (directMatch != null) {
            handlers.add(0, directMatch);
            return handlers;
        } else if (iteration > handlerList.size()) {
        // check for too many iterations
            String message = String.format("No handler chain can be assembled for fromUnit: %s toUnit: %s", source, target);
            throw new ConversionPathException(message);
        }

        // look for step matches
        for (ConversionHandler handler : handlerList) {
            if (handler.convertsTo(target)) {
                LOG.info(".... ... handlere: [{}]", handler.getClass().getName());
                LOG.info(".... ... matches target: [{}]", target);
                List<ConversionSpec> potentialTargets = handler.getAllSources().stream()
                        .filter(pt -> !pt.equals(target)).collect(Collectors.toList());

                for (ConversionSpec iterationTarget: potentialTargets) {
                    if (checkedPairs.contains(new HandlerChainKey(source, target))) {
                        LOG.info(".... ... ... skipping pair: [{}]", source);
                        LOG.info(".... ... ... skipping pair: [{}]", target);
                        continue;
                    }
                    checkedPairs.add(new HandlerChainKey(source, target));
                    // skip if already checked
                    List<ConversionHandler> foundList = assembleHandlerList(source, iterationTarget, handlers, iteration + 1, checkedPairs);
                    if ( !foundList.isEmpty()) {
                        foundList.add(handler);
                        return foundList;
                    }        // add pair to checkedPair
                }

            }
        }
        return new ArrayList<>();
    }



    private ConversionHandler findHandlerMatch(ConversionSpec source, ConversionSpec target) {
        return handlerList.stream()
                .filter(h -> h.handles(source, target))
                .findFirst().orElse(null);
    }

    private boolean checkTargetEqualsSource(ConversionSpec source, ConversionSpec target) {
        return (source.equals(target));
    }
}
