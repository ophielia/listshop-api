package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.api.model.CategoryType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by margaretmartin on 30/10/2017.
 */
@Configuration
@ConfigurationProperties(prefix = "shopping.list.properties")
public class ShoppingListProperties {

    private String testValue;


    private String frequentCategoryName = "frequent";
    private Integer frequentIdAndSort = -2;
    private String uncategorizedCategoryName = "non-cat";
    private Integer uncategorizedIdAndSort = 999;
    private Integer highlightIdAndSort = -1;
    private final Integer highlightListIdAndSort = -3;
    private final Long defaultIdAndSort = 800L;
    private Map<CategoryType, String> nameMapByType = null;
    private Map<CategoryType, Long> idMapByType = null;

    public String getTestValue() {
        return testValue;
    }


    public void setTestValue(String testValue) {
        this.testValue = testValue;
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
        return Long.valueOf(getFrequentIdAndSort());
    }

    public Long getUncategorizedIdAndSortAsLong() {
        return Long.valueOf(getUncategorizedIdAndSort());
    }

    public Integer getHighlightIdAndSort() {
        return highlightIdAndSort;
    }

    public Long getHighlightIdAndSortAsLong() {
        return Long.valueOf(highlightIdAndSort);
    }

    public Long getHighlightListIdAndSortAsLong() {
        return Long.valueOf(highlightListIdAndSort);
    }

    public Integer getHighlightListIdAndSort() {
        return highlightListIdAndSort;
    }

    public void setHighlightIdAndSort(Integer highlightIdAndSort) {
        this.highlightIdAndSort = highlightIdAndSort;
    }

    public String getCategoryNameByType(CategoryType categoryType) {
        if (nameMapByType == null) {
            createNameMapByType();
        }
        if (nameMapByType.containsKey(categoryType)) {
            return nameMapByType.get(categoryType);
        }
        return null;
    }

    private void createNameMapByType() {
        nameMapByType = new EnumMap<CategoryType, String>(CategoryType.class);
        nameMapByType.put(CategoryType.Frequent, getFrequentCategoryName());
        nameMapByType.put(CategoryType.UnCategorized, getUncategorizedCategoryName());
    }

    public Long getIdAndSortByType(CategoryType categoryType) {
        if (idMapByType == null) {
            createIdMapByType();
        }
        if (idMapByType.containsKey(categoryType)) {
            return idMapByType.get(categoryType);
        }
        return defaultIdAndSort;
    }

    private void createIdMapByType() {
        idMapByType = new EnumMap<CategoryType, Long>(CategoryType.class);
        idMapByType.put(CategoryType.Frequent, getFrequentIdAndSortAsLong());
        idMapByType.put(CategoryType.UnCategorized, getUncategorizedIdAndSortAsLong());
        idMapByType.put(CategoryType.Highlight, getHighlightIdAndSortAsLong());
        idMapByType.put(CategoryType.HighlightList, getHighlightListIdAndSortAsLong());
    }


}
