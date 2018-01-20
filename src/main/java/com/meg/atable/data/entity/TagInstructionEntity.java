package com.meg.atable.data.entity;

import com.meg.atable.service.tag.AutoTagSubject;
import com.meg.atable.service.Instruction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@DiscriminatorValue("Tag")
public class TagInstructionEntity extends AutoTagInstructionEntity implements Instruction {
@Transient
    private List<Integer> inflatedSearchTerms;

    @Override
    public Long getTagIdToAssign(AutoTagSubject subject) {
        if (inflatedSearchTerms == null) {
            inflateSearchTerms();
        }
        // determine if search term match exists
        List<Integer> matches = inflatedSearchTerms.stream()
                .filter(st -> subject.getTagFlags().contains(st))
                .collect(Collectors.toList());
        if (getInvert() && matches.isEmpty()) {
            return getAssignTagId();
        }
        if (!getInvert() & matches.size()>0) {
            return getAssignTagId();
        }
        return null;
    }

    private void inflateSearchTerms() {
        String toinflate = getSearchTerms();
        String[] terms = toinflate.split(";");
        if (terms == null || terms.length == 0) {
            inflatedSearchTerms =  new ArrayList<>();
        }
        inflatedSearchTerms =  Arrays.stream(terms).map(Integer::valueOf).collect(Collectors.toList());

    }
}
