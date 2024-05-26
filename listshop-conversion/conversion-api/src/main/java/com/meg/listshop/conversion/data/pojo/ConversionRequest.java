package com.meg.listshop.conversion.data.pojo;

public class ConversionRequest {
    private final ConversionTargetType contextType;

    private final DomainType domainType;

    public ConversionRequest(ConversionTargetType contextType, DomainType domainType) {
        this.contextType = contextType;
        this.domainType = domainType;
    }

    public ConversionTargetType getContextType() {
        return contextType;
    }



    public DomainType getDomainType() {
        return domainType;
    }

    @Override
    public String toString() {
        return "ConversionContext{" +
                "contextType=" + contextType +
                ", domainType=" + domainType +
                '}';
    }
}
