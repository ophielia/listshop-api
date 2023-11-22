package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitType domain) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for domain [{}], amount [{}]", domain, amount);
        ConversionSpec source = ConversionSpec.fromExactUnit(amount.getUnit());
        ConversionSpec target = ConversionSpec.convertedFromUnit(amount.getUnit());

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for context [{}], amount [{}]", context, amount);
        ConversionSpec source = ConversionSpec.fromExactUnit(amount.getUnit());
        ConversionSpec target = ConversionSpec.fromContextAndSource(context, amount.getUnit());


        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);

    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, UnitEntity targetUnit) throws ConversionPathException, ConversionFactorException {
        LOG.debug("Beginning convert for unit [{}], amount [{}]", targetUnit, amount);
        ConversionSpec source = ConversionSpec.fromExactUnit(amount.getUnit());
        ConversionSpec target = ConversionSpec.fromExactUnit(targetUnit);

        if (checkTargetEqualsSource(source, target)) {
            LOG.info("No conversion to do - source [{}] and target [{}] are equal. ", source, target);
            return amount;
        }

        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(source, target);

        // return converted amount
        return chain.process(amount, target);
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
        LOG.trace("Creating chain for source: [{}], target [{}]", sourceSpec, targetSpec);
        // assemble handler chain list
        List<ConversionHandler> handlers = assembleHandlerList(sourceSpec, targetSpec, new ArrayList<>(), 0);

        // convert list into handler chain
        if (handlers == null || handlers.isEmpty()) {
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

    private List<ConversionHandler> assembleHandlerList(ConversionSpec source, ConversionSpec target, List<ConversionHandler> handlers, int iteration) throws ConversionPathException {
        // look for direct match
        ConversionHandler directMatch = findHandlerMatch(source, target);
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
        for (ConversionHandler handler : handlerList) {
            if (handler.convertsTo(target)) {
                List<ConversionHandler> foundList = assembleHandlerList(source, target, handlers, iteration + 1);
                if (foundList != null) {
                    foundList.add(handler);
                    return foundList;
                }
            }
        }
        return null;
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
