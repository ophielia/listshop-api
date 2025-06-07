package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingListItem {

    @JsonProperty("item_id")
    private Long item_id;

    private Tag tag;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("removed")
    private Date removed;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("tag_name")
    private String tagName;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    @JsonProperty("list_id")
    private String listId;

    @JsonProperty("used_count")
    private Integer usedCount;

    @JsonProperty("source_keys")
    private List<String> sourceKeys;

    @JsonIgnore
    private String rawSourceKeys;

    @JsonIgnore
    private Set<String> handles = new HashSet<>();

    @JsonIgnore
    private String rawListSources;

    @JsonIgnore
    private String rawDishSources;

    @JsonIgnore
    private List<ListItemSource> sources = new ArrayList<>();

    public ShoppingListItem(Long id) {
        this.item_id = id;
        this.tag = new Tag();
    }

    public ShoppingListItem() {
        // necessary for json construction
        this.tag = new Tag();
    }

    @JsonIgnore
    public Long getId() {
        return item_id;
    }


    public Tag getTag() {
        return tag;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public ShoppingListItem addedOn(Date addedOn) {
        this.addedOn = addedOn;
        return this;
    }


    public String getTagName() {
        return this.tag != null ? this.tag.getName() : null;
    }

    public ShoppingListItem tagName(String tagName) {
        this.tag.name(tagName);
        return this;
    }

    @JsonIgnore
    public String getTagType() {
        return this.tag != null ? this.tag.getTagType() : null;
    }

    public ShoppingListItem tagType(String tagType) {
        this.tag.tagType(tagType);
        return this;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public ShoppingListItem crossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
        return this;
    }


    public String getListId() {
        return listId;
    }


    public ShoppingListItem listId(String listId) {
        this.listId = listId;
        return this;
    }

    @JsonIgnore
    public String getTagId() {
        return tag != null ? tag.getId() : null;
    }

    public ShoppingListItem tagId(String tagId) {
        this.tag.setTag_id(tagId);
        return this;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public ShoppingListItem usedCount(Integer usedCount) {
        this.usedCount = usedCount;
        return this;
    }

    public Date getRemoved() {
        return removed;
    }

    public ShoppingListItem removed(Date created) {
        this.removed = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public ShoppingListItem updated(Date updated) {
        this.updated = updated;
        return this;
    }

    public ShoppingListItem sourceKeys(List<String> sourceKeys) {
        this.sourceKeys = sourceKeys;
        return this;
    }

    public List<String> getSourceKeys() {
        return sourceKeys;
    }

    public String getRawSourceKeys() {
        return rawSourceKeys;
    }

    public ShoppingListItem rawSourceKeys(String rawSourceKeys) {
        this.rawSourceKeys = rawSourceKeys;
        return this;
    }

    public String getRawListSources() {
        return rawListSources;
    }

    public ShoppingListItem rawListSources(String rawListSources) {
        this.rawListSources = rawListSources;
        return this;
    }

    public String getRawDishSources() {
        return rawDishSources;
    }

    public ShoppingListItem rawDishSources(String rawDishSources) {
        this.rawDishSources = rawDishSources;
        return this;
    }

    public Set<String> getHandles() {
        return handles;
    }

    public ShoppingListItem handles(Set<String> handles) {
        this.handles = handles;
        return this;
    }

    public void addHandle(String handle) {
        if (this.handles == null) {
            this.handles = new HashSet<>();
        }
        handles.add(handle);
    }


    public List<ListItemSource> getSources() {
        return sources;
    }

    public ShoppingListItem sources(List<ListItemSource> sources) {
        if (sources == null || sources.size() == 0) {
            return this;
        }
        this.sources = sources;
        return this;
    }
}
