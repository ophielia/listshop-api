package com.meg.listshop.conversion.service;


import com.meg.listshop.conversion.data.entity.Unit;
import com.meg.listshop.conversion.data.pojo.ConversionContext;
import com.meg.listshop.conversion.data.pojo.ConversionContextType;
import com.meg.listshop.conversion.data.pojo.MeasurementDomain;
import com.meg.listshop.conversion.data.pojo.UnitType;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConversionServiceImpl implements ConversionService {

    HashMap<HandlerChainKey, HandlerChain> chainMap;
    private final List<ConversionHandler> handlerList;

    public ConversionServiceImpl(List<ConversionHandler> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, MeasurementDomain domain) {
        return null;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, ConversionContext context) {
        return null;
    }

    @Override
    public ConvertibleAmount convert(ConvertibleAmount amount, Unit targetUnit) throws ConversionPathException, ConversionFactorException {
        // find or create handler chain for source / target
        HandlerChain chain = getOrCreateChain(amount.getUnit(), targetUnit, null);

        // return converted amount
        return chain.process(amount);
    }

    private HandlerChain getOrCreateChain(Unit fromUnit, Unit toUnit, ConversionContextType context) throws ConversionPathException {
        HandlerChainKey conversionKey = new HandlerChainKey(fromUnit.getType(), toUnit.getType(), context);

        if (chainMap.containsKey(conversionKey)) {
            return chainMap.get(conversionKey);
        }

        HandlerChain newChain = createConversionChain(fromUnit, toUnit);
        chainMap.put(conversionKey, newChain);
        return newChain;
    }

    private HandlerChain createConversionChain(Unit fromUnit, Unit toUnit) throws ConversionPathException {
        // assemble handler chain list
        List<ConversionHandler> handlers = assembleHandlerList(fromUnit.getType(), toUnit.getType(), new ArrayList<>(), 0);

        // convert list into handler chain
        if (handlers.isEmpty()) {
            String message = String.format("No handler chain can be assembled for fromUnit: %s toUnit: %s", fromUnit.getType(), toUnit.getType());
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

    private List<ConversionHandler> assembleHandlerList(UnitType fromUnit, UnitType toUnit, List<ConversionHandler> handlers, int iteration) throws ConversionPathException {
        // look for direct match
        ConversionHandler directMatch = findHandlerMatch(fromUnit, toUnit);
        if (directMatch != null) {
            handlers.add(0, directMatch);
            return handlers;
        }
        // check for too many iterations
        if (iteration > handlerList.size()) {
            String message = String.format("No handler chain can be assembled for fromUnit: %s toUnit: %s", fromUnit, toUnit);
            throw new ConversionPathException(message);
        }

        // look for step matches
        for (ConversionHandler handler : handlerList) {
            if (handler.convertsTo(toUnit)) {
                List<ConversionHandler> foundList = assembleHandlerList(fromUnit, toUnit, handlers, iteration + 1);
                if (foundList != null) {
                    foundList.add(handler);
                    return foundList;
                }
            }
        }
        return null;
    }

    private ConversionHandler findHandlerMatch(UnitType fromUnit, UnitType toUnit) {
        return handlerList.stream()
                .filter(h -> h.handles(fromUnit, toUnit))
                .findFirst().orElse(null);
    }
}
