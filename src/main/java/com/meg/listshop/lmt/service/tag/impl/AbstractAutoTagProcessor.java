package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.service.Instruction;
import com.meg.listshop.lmt.service.tag.AutoTagProcessor;
import com.meg.listshop.lmt.service.tag.AutoTagSubject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by margaretmartin on 07/12/2017.
 */
@Service
public abstract class AbstractAutoTagProcessor implements AutoTagProcessor {

    long filledOnMilliseconds;

    @Value("${service.autotag.cache.expiresafter.minutes}")
    private long expiresAfterMinutes;

    private long staleMilliseconds;

    @PostConstruct
    public void setMilliseconds() {
        staleMilliseconds = 60 * expiresAfterMinutes * 1000;
    }

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



    public List<Instruction> getInstructions() {
        long now = System.currentTimeMillis();
        if (now - staleMilliseconds < filledOnMilliseconds) {
            return currentInstructions();
        }
        fillInstructions();
        filledOnMilliseconds = now;
        return currentInstructions();
    }

    protected abstract List<Instruction> currentInstructions();
    protected abstract List<Instruction> fillInstructions();

}
