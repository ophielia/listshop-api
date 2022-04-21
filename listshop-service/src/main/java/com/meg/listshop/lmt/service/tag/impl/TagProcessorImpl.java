package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagInstructionEntity;
import com.meg.listshop.lmt.data.repository.TagInstructionRepository;
import com.meg.listshop.lmt.service.Instruction;
import com.meg.listshop.lmt.service.tag.AutoTagProcessor;
import com.meg.listshop.lmt.service.tag.AutoTagSubject;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class TagProcessorImpl extends AbstractAutoTagProcessor {
    TagInstructionRepository tagInstructionRepository;

    TagStructureService tagStructureService;

    private List<Instruction> instructions;

    @Autowired
    public TagProcessorImpl(TagInstructionRepository tagInstructionRepository, TagStructureService tagStructureService) {
        this.tagInstructionRepository = tagInstructionRepository;
        this.tagStructureService = tagStructureService;
    }

    @Override
    public Long getProcessIdentifier() {
        return AutoTagProcessor.Type.TAG;
    }

    @Override
    public List<Instruction> fillInstructions() {
        List<TagInstructionEntity> entities = tagInstructionRepository.findAll();
        for (TagInstructionEntity instruction : entities) {
            Set<Long> tagIdList = instruction.getMasterSearchTags();
            // get all group tags for these search tags
            Set<Long> searchIds = tagStructureService.getDescendantsTagIds(tagIdList);
            instruction.setSearchTags(searchIds);
        }
        instructions = new ArrayList<>();
        instructions.addAll(entities);
        return instructions;
    }

    @Override
    public Long processTagForInstruction(Instruction instr, AutoTagSubject subject) {
        TagInstructionEntity instruction = (TagInstructionEntity) instr;
        // determine if search term match exists
        List<Long> matches = instruction.getSearchTags().stream()
                .filter(st -> subject.getTagIdsForDish().contains(st))
                .collect(Collectors.toList());
        if (instruction.getInvert() && matches.isEmpty()) {
            List<Long> filterMatch = instruction.getInvertExclusions().stream()
                    .filter(t -> subject.getTagIdsForDish().contains(t))
                    .collect(Collectors.toList());
            if (filterMatch.isEmpty()) {
                return instruction.getAssignTagId();
            }
        }
        if (!instruction.getInvert() && !matches.isEmpty()) {
            return instruction.getAssignTagId();
        }
        return null;


    }

    protected List<Instruction> currentInstructions() {
        return instructions;
    }
}
