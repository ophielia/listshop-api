package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.List;

/**
 * Created by margaretmartin on 26/11/2017.
 */
public class TagSearchCriteria {

    private Long userId;
    private List<TagType> tagTypes;
    private TagFilterType tagFilterType;

    public TagSearchCriteria(Long userId, List<TagType> tagTypes, TagFilterType tagFilterType) {
        this.userId = userId;
        this.tagTypes = tagTypes;
        this.tagFilterType = tagFilterType;
    }

    public TagSearchCriteria() {
    }

    public Long getUserId() {
        return userId;
    }

    public List<TagType> getTagTypes() {
        return tagTypes;
    }

    public TagFilterType getTagFilterType() {
        return tagFilterType;
    }

    public TagSearchCriteria userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public TagSearchCriteria tagTypes(List<TagType> tagTypes) {
        this.tagTypes = tagTypes;
        return this;
    }

    public TagSearchCriteria tagFilterType(TagFilterType tagFilterType) {
        this.tagFilterType = tagFilterType;
        return this;
    }
}
