package com.meg.atable.data.entity;

import com.meg.atable.service.tag.AutoTagSubject;
import com.meg.atable.service.Instruction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Entity
@DiscriminatorValue("Text")
public class TextInstructionEntity extends AutoTagInstructionEntity implements Instruction {

@Transient
    private List<String> inflatedSearchTerms;

    @Override
    public Long getTagIdToAssign(AutoTagSubject subject) {
        if (inflatedSearchTerms == null) {
            inflateSearchTerms();
        }
        // determine if search term match exists
        boolean match = false;
        for (String term: inflatedSearchTerms) {
            match = subject.getDish().getDishName().toLowerCase().contains(term);
            if (subject.getDish().getDescription()!=null)  {
                match |= subject.getDish().getDescription().toLowerCase().contains(term);
            }
            if (match == true) {
                break;
            }
        }
        if (getInvert() && !match) {
            return getAssignTagId();
        }
        if (!getInvert() & match) {
            return getAssignTagId();
        }
        return null;
    }

    private void inflateSearchTerms() {
        String toinflate = getSearchTerms().toLowerCase();
        String[] terms = toinflate.split(";");
        if (terms == null || terms.length == 0) {
            inflatedSearchTerms =  new ArrayList<>();
        }
        inflatedSearchTerms =  Arrays.asList(terms);
    }

}
