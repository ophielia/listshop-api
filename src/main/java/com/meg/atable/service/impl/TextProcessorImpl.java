package com.meg.atable.service.impl;

import com.meg.atable.data.entity.TagInstructionEntity;
import com.meg.atable.data.entity.TextInstructionEntity;
import com.meg.atable.data.repository.TagInstructionRepository;
import com.meg.atable.data.repository.TextInstructionRepository;
import com.meg.atable.service.Instruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class TextProcessorImpl extends AbstractAutoTagProcessor  {

    @Autowired
    TextInstructionRepository textInstructionRepository;

    private List<Instruction> instructions = null;

    @Override
    protected Long getProcessIdentifier() {
        return Type.Tag;
    }

    @Override
    protected List<Instruction> getInstructions() {
        if (instructions == null) {
            List<TextInstructionEntity> entities = textInstructionRepository.findAll();
            instructions = new ArrayList<>();
            instructions.addAll(entities);
        }
        return instructions;
    }
}
