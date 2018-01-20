package com.meg.atable.service.tag.impl;

import com.meg.atable.service.tag.AutoTagProcessor;
import com.meg.atable.service.tag.AutoTagSubject;
import com.meg.atable.service.Instruction;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by margaretmartin on 07/12/2017.
 */
@Service
public abstract class AbstractAutoTagProcessor implements AutoTagProcessor {

    @Override
    public AutoTagSubject autoTagSubject(AutoTagSubject subject) {
        // check eligibility
        if (!isEligible(subject)) {
            return subject;
        }
        // go through instructions
        if (getInstructions() == null) {
            return subject;
        }
        for (Instruction instruction : getInstructions()) {

            // check if assign tag is available
            Long tagId = instruction.getTagIdToAssign(subject);
            // add to subject if available
            if (tagId == null) {
                continue;
            }
            subject.addToTagIdsToAssign(tagId);
        }

        // mark processor as done
        subject.addProcessedBy(getProcessIdentifier());
        return subject;
    }

    private boolean isEligible(AutoTagSubject subject) {
        if (subject.isOverrideFlag()) {
            return true;
        }
        return !subject.hasBeenProcessedBy(getProcessIdentifier());
    }

    protected abstract Long getProcessIdentifier();


    protected abstract List<Instruction> getInstructions();

}
