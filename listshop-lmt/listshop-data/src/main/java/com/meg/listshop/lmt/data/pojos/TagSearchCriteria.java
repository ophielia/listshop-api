package com.meg.listshop.lmt.data.pojos;

import com.meg.listshop.lmt.api.model.TagFilterType;
import com.meg.listshop.lmt.api.model.TagType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 26/11/2017.
 */
public class TagSearchCriteria {

    private Long userId;
    private List<TagType> tagTypes;
    private TagFilterType tagFilterType;

    private List<TagInternalStatus> excludedStatuses = new ArrayList<>();
    private List<TagInternalStatus> includedStatuses = new ArrayList<>();

    private IncludeType groupIncludeType;


    public TagSearchCriteria(Long userId, List<TagType> tagTypes, List<TagInternalStatus> excludedStatuses, List<TagInternalStatus> includedStatuses, IncludeType groupIncludeType) {
        this.userId = userId;
        this.tagTypes = tagTypes;
        this.excludedStatuses = excludedStatuses;
        if (includedStatuses != null) {
            this.includedStatuses = includedStatuses;
        }
        if (groupIncludeType != null) {
        this.groupIncludeType = groupIncludeType;
        }
    }

    public TagSearchCriteria() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<TagType> getTagTypes() {
        return tagTypes;
    }

    public void setTagTypes(List<TagType> tagTypes) {
        this.tagTypes = tagTypes;
    }

    public TagFilterType getTagFilterType() {
        return tagFilterType;
    }

    public void setTagFilterType(TagFilterType tagFilterType) {
        this.tagFilterType = tagFilterType;
    }

    public List<TagInternalStatus> getExcludedStatuses() {
        return excludedStatuses;
    }

    public void setExcludedStatuses(List<TagInternalStatus> excludedStatuses) {
        this.excludedStatuses = excludedStatuses;
    }

    public List<TagInternalStatus> getIncludedStatuses() {
        return includedStatuses;
    }

    public void setIncludedStatuses(List<TagInternalStatus> includedStatuses) {
        this.includedStatuses = includedStatuses;
    }

    public IncludeType getGroupIncludeType() {
        return groupIncludeType;
    }

    public void setGroupIncludeType(IncludeType groupIncludeType) {
        this.groupIncludeType = groupIncludeType;
    }
}
