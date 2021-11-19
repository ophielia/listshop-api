package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.data.entity.TextInstructionEntity;
import com.meg.listshop.lmt.data.repository.TextInstructionRepository;
import com.meg.listshop.lmt.service.Instruction;
import com.meg.listshop.lmt.service.tag.AutoTagProcessor;
import com.meg.listshop.lmt.service.tag.AutoTagSubject;
import com.meg.listshop.lmt.service.tag.impl.AbstractAutoTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class TextProcessorImpl extends AbstractAutoTagProcessor {

    @Autowired
    TextInstructionRepository textInstructionRepository;

    List<Instruction> instructions = null;

    @Override
    public Long getProcessIdentifier() {
        return AutoTagProcessor.Type.TEXT;
    }

    @Override
    protected List<Instruction> fillInstructions() {
        if (instructions == null) {
            List<TextInstructionEntity> entities = textInstructionRepository.findAll();
            instructions = new ArrayList<>();
            instructions.addAll(entities);
        }
        return instructions;
    }

    @Override
    protected Long processTagForInstruction(Instruction instr, AutoTagSubject subject) {
        TextInstructionEntity instruction = (TextInstructionEntity)instr;



        // determine if search term match exists
        boolean match = false;
        for (String term: instruction.getTextSearchTerms()) {
            if (subject.getDish().getDishName() != null) {
                match = subject.getDish().getDishName().toLowerCase().contains(term);
            }
            if (subject.getDish().getDescription()!=null)  {
                match |= subject.getDish().getDescription().toLowerCase().contains(term);
            }
            if (match) {
                break;
            }
        }
        if (instruction.getInvert() && !match) {
            return instruction.getAssignTagId();
        }
        if (!instruction.getInvert() && match) {
            return instruction.getAssignTagId();
        }

        return null;
    }

    protected List<Instruction> currentInstructions() {
        return instructions;
    }
    }
