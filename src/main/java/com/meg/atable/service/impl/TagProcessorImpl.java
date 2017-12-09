package com.meg.atable.service.impl;

import com.meg.atable.data.entity.TagInstructionEntity;
import com.meg.atable.data.repository.TagInstructionRepository;
import com.meg.atable.service.AutoTagProcessor;
import com.meg.atable.service.Instruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class TagProcessorImpl extends AbstractAutoTagProcessor {

    @Autowired
    TagInstructionRepository tagInstructionRepository;

    private List<Instruction> instructions;

    @Override
    protected Long getProcessIdentifier() {
        return AutoTagProcessor.Type.Tag;
    }

    @Override
    protected List<Instruction> getInstructions() {
        if (instructions == null) {
            List<TagInstructionEntity> entities = tagInstructionRepository.findAll();
            instructions = new ArrayList<>();
            instructions.addAll(entities);
        }
        return instructions;
    }
}
