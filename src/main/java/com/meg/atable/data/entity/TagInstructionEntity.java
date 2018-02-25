package com.meg.atable.data.entity;

import com.meg.atable.service.Instruction;
import com.meg.atable.service.tag.AutoTagSubject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@DiscriminatorValue("Tag")
public class TagInstructionEntity extends AutoTagInstructionEntity implements Instruction {
    @Transient
    private Set<Long> inflatedSearchTerms;

    @Transient
    private Set<Long> searchTags;
    @Transient
    private List<Long> invertExclusions;

    @Override
    public Long getTagIdToAssign(AutoTagSubject subject) {
        if (inflatedSearchTerms == null) {
            inflateSearchTerms();
        }
        // determine if search term match exists
        List<Long> matches = searchTags.stream()
                .filter(st -> subject.getTagIdsForDish().contains(st))
                .collect(Collectors.toList());
        if (getInvert() && matches.isEmpty()) {
            return getAssignTagId();
        }
        if (!getInvert() & matches.size() > 0) {
            return getAssignTagId();
        }
        return null;
    }

    public Set<Long> getMasterSearchTags() {
        if (inflatedSearchTerms == null) {
            inflateSearchTerms();
        }
        return inflatedSearchTerms;
    }

    private void inflateSearchTerms() {

        String toinflate = getSearchTerms();
        String[] terms = toinflate.split(";");
        if (terms == null || terms.length == 0) {
            inflatedSearchTerms = new HashSet<>();
        }
        inflatedSearchTerms = Arrays.stream(terms).map(Long::valueOf).collect(Collectors.toSet());

    }


    public Set<Long> getSearchTags() {
        return searchTags;
    }

    public void setSearchTags(Set<Long> searchTags) {
        this.searchTags = searchTags;
    }

    public List<Long> getInvertExclusions() {
        if (invertExclusions == null) {
            String toinflate = getInvertFilter();
            String[] terms = toinflate.split(";");
            if (terms == null || terms.length == 0) {
                invertExclusions = new ArrayList<>();
            }
            invertExclusions = Arrays.stream(terms).map(Long::valueOf).collect(Collectors.toList());
        }
        return invertExclusions;
    }
}
