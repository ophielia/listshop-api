package com.meg.atable.service;

import com.meg.atable.api.model.ListLayout;
import com.meg.atable.api.model.ListType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Component
@ConfigurationProperties(prefix="shopping.list.properties")
public class ShoppingListProperties {

    private String testValue;

    /*private Map<String,String> rawDefaultLayouts;
*/
    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }
/*
    public Map<String, String> getRawDefaultLayouts() {
        return rawDefaultLayouts;
    }

    public void setRawDefaultLayouts(Map<String, String> rawDefaultLayouts) {
        this.rawDefaultLayouts = rawDefaultLayouts;
    }*/
}
