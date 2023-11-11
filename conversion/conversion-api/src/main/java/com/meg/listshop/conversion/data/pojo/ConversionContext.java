package com.meg.listshop.conversion.data.pojo;

public class ConversionContext {
    private ConversionContextType contextType;

    private MeasurementDomain measurementDomain;

    public ConversionContext(ConversionContextType contextType, MeasurementDomain measurementDomain) {
        this.contextType = contextType;
        this.measurementDomain = measurementDomain;
    }

    public ConversionContextType getContextType() {
        return contextType;
    }

    public MeasurementDomain getMeasurementDomain() {
        return measurementDomain;
    }
}
