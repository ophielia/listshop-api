package com.meg.listshop.conversion.service;

import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ExceedsAllowedScaleException;
import com.meg.listshop.conversion.service.handlers.ConversionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerChain {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerChain.class);
    private ConversionHandler handler;

    private HandlerChain nextLink;

    public HandlerChain(ConversionHandler handler) {
        this.handler = handler;
    }

    public ConvertibleAmount process(ConvertibleAmount toConvert, ConversionSpec target) throws ConversionFactorException , ExceedsAllowedScaleException {
        LOG.debug("Starting chain conversion from: [{}], to: [{}], handler: [{}]", toConvert.getUnit(), target, handler);
        ConvertibleAmount converted = handler.convert(toConvert, target);
        if (nextLink == null) {
            LOG.debug("End of chain, returning amount [{}]", converted);
            return converted;
        }

        return nextLink.process(converted, target);
    }

    public void setNextLink(HandlerChain nextLink) {
        this.nextLink = nextLink;
    }
}
