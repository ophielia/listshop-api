package com.meg.listshop.conversion.data.pojo;

public class ConversionRequest {
    private final ConversionTargetType contextType;

    private final DomainType domainType;

    private String unitSize;

    public ConversionRequest(ConversionTargetType contextType, DomainType domainType) {
        this(contextType,domainType,null);
    }

    public ConversionRequest(ConversionTargetType contextType, DomainType domainType, String unitSize) {
        this.contextType = contextType;
        this.domainType = domainType;
        this.unitSize = unitSize;
    }

    public ConversionTargetType getContextType() {
        return contextType;
    }



    public DomainType getDomainType() {
        return domainType;
    }

    public String getUnitSize() {
        return unitSize;
    }

    @Override
    public String toString() {
        return "ConversionContext{" +
                "contextType=" + contextType +
                "unitSize=" + unitSize +
                ", domainType=" + domainType +
                '}';
    }
}
