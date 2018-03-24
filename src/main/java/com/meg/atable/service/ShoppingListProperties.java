package com.meg.atable.service;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.api.model.ListType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Configuration
@ConfigurationProperties(prefix = "shopping.list.properties")
public class ShoppingListProperties {

    private String testValue;

    private final Map<String, String> rawDefaultLayouts = new HashMap<String, String>();
    private Map<ListType, ListLayoutType> defaultLayouts;

    private String frequentCategoryName = "frequent";
    private Integer frequentIdAndSort = -2;
    private String uncategorizedCategoryName = "non-cat";
    private Integer uncategorizedIdAndSort = 999;
    private Integer highlightIdAndSort = -1;

    public String getTestValue() {
        return testValue;
    }


    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    public Map<String, String> getRawDefaultLayouts() {
        return rawDefaultLayouts;
    }

    public Map<ListType, ListLayoutType> getDefaultLayouts() {
        this.defaultLayouts = new HashMap<ListType, ListLayoutType>();
        for (Map.Entry<String, String> entry : this.rawDefaultLayouts.entrySet()) {
            ListType listType = ListType.valueOf(entry.getKey());
            ListLayoutType listLayoutType = ListLayoutType.valueOf(entry.getValue());
            defaultLayouts.put(listType, listLayoutType);
        }
        return this.defaultLayouts;
    }

    public String getFrequentCategoryName() {
        return frequentCategoryName;
    }

    public void setFrequentCategoryName(String frequentCategoryName) {
        this.frequentCategoryName = frequentCategoryName;
    }

    public Integer getFrequentIdAndSort() {
        return frequentIdAndSort;
    }

    public void setFrequentIdAndSort(Integer frequentIdAndSort) {
        this.frequentIdAndSort = frequentIdAndSort;
    }

    public String getUncategorizedCategoryName() {
        return uncategorizedCategoryName;
    }

    public void setUncategorizedCategoryName(String uncategorizedCategoryName) {
        this.uncategorizedCategoryName = uncategorizedCategoryName;
    }

    public Integer getUncategorizedIdAndSort() {
        return uncategorizedIdAndSort;
    }

    public void setUncategorizedIdAndSort(Integer uncategorizedIdAndSort) {
        this.uncategorizedIdAndSort = uncategorizedIdAndSort;
    }

    public Long getFrequentIdAndSortAsLong() {
        return new Long(getFrequentIdAndSort());
    }

    public Long getUncategorizedIdAndSortAsLong() {
        return new Long(getUncategorizedIdAndSort());
    }

    public Integer getHighlightIdAndSort() {
        return highlightIdAndSort;
    }

    public Long getHighlightIdAndSortAsLong() {
        return new Long(highlightIdAndSort);
    }

    public void setHighlightIdAndSort(Integer highlightIdAndSort) {
        this.highlightIdAndSort = highlightIdAndSort;
    }
}
