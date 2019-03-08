package com.meg.atable.lmt.data.entity;

import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.Instruction;

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
@DiscriminatorValue("TEXT")
public class TextInstructionEntity extends AutoTagInstructionEntity implements Instruction {

@Transient
    private List<String> textSearchTerms;

    @Override
    public Long assignTag(AutoTagSubject subject) {
        if (textSearchTerms == null) {
            inflateSearchTerms();
        }
        // determine if search term match exists
        boolean match = false;
        for (String term: textSearchTerms) {
            match = subject.getDish().getDishName().toLowerCase().contains(term);
            if (subject.getDish().getDescription()!=null)  {
                match |= subject.getDish().getDescription().toLowerCase().contains(term);
            }
            if (match) {
                break;
            }
        }
        if (getInvert() && !match) {
            return getAssignTagId();
        }
        if (!getInvert() && match) {
            return getAssignTagId();
        }
        return null;
    }

    private void inflateSearchTerms() {
        String toinflate = getSearchTerms().toLowerCase();
        String[] terms = toinflate.split(";");
        if (terms == null || terms.length == 0) {
            textSearchTerms =  new ArrayList<>();
        }
        textSearchTerms =  Arrays.asList(terms);
    }

    public List<String> getTextSearchTerms() {
        if (textSearchTerms == null) {
            inflateSearchTerms();
        }
        return textSearchTerms;
    }

    public void setTextSearchTerms(List<String> textSearchTerms) {
        this.textSearchTerms = textSearchTerms;
    }
}
