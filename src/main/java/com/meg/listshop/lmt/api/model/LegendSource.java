package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class LegendSource {

    private String key;
    private String display;


    public LegendSource() {
        // empty constructor for jackson
    }

    public LegendSource(String key, String display) {
        this.key = key;
        this.display = display;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
