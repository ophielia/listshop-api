package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.service.tag.AutoTagProcessor;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.Instruction;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by margaretmartin on 07/12/2017.
 */
@Service
public abstract class AbstractAutoTagProcessor implements AutoTagProcessor {

    long filledOnMilliseconds;

    private static final long STALEMILLIS = 60 * 5 * 1000;

    @Override
    public AutoTagSubject autoTagSubject(AutoTagSubject subject) {
        // check eligibility
        if (!isEligible(subject)) {
            return subject;
        }
        // go through instructions
        if (fillInstructions() == null) {
            return subject;
        }
        for (Instruction instruction : getInstructions()) {
            // check if assign tag is available
            Long tagId = processTagForInstruction(instruction,subject);
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

    protected abstract Long processTagForInstruction(Instruction instruction, AutoTagSubject subject);

    private boolean isEligible(AutoTagSubject subject) {
        if (subject.isOverrideFlag()) {
            return true;
        }
        return !subject.hasBeenProcessedBy(getProcessIdentifier());
    }



    List<Instruction> getInstructions() {
        long now = System.currentTimeMillis();
        if (now - STALEMILLIS < filledOnMilliseconds) {
            return currentInstructions();
        }
        fillInstructions();
        filledOnMilliseconds = now;
        return currentInstructions();
    }

    protected abstract List<Instruction> currentInstructions();
    protected abstract List<Instruction> fillInstructions();

}
