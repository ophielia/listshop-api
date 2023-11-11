package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;

public class HandlerChain {

    private ConversionHandler handler;

    private HandlerChain nextLink;

    public HandlerChain(ConversionHandler handler) {
        this.handler = handler;
    }

    public ConvertibleAmount process(ConvertibleAmount toConvert) throws ConversionFactorException {
        ConvertibleAmount converted = handler.convert(toConvert);
        if (nextLink == null) {
            return converted;
        }
        return nextLink.process(converted);
    }

    public void setNextLink(HandlerChain nextLink) {
        this.nextLink = nextLink;
    }
}
