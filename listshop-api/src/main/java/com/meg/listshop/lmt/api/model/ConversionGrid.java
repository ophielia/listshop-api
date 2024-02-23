package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ConversionGrid {

    List<ConversionSample> samples;

    public ConversionGrid() {
    }

    public List<ConversionSample> getSamples() {
        return samples;
    }

    public void setSamples(List<ConversionSample> samples) {
        this.samples = samples;
    }

    @Override
    public String toString() {
        return "ConversionGrid{" +
                "samples=" + samples +
                '}';
    }
}
