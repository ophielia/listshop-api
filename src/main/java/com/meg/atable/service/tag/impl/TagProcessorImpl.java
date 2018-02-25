package com.meg.atable.service.tag.impl;

import com.meg.atable.data.entity.TagInstructionEntity;
import com.meg.atable.data.repository.TagInstructionRepository;
import com.meg.atable.service.tag.AutoTagProcessor;
import com.meg.atable.service.Instruction;
import com.meg.atable.service.tag.AutoTagSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class TagProcessorImpl extends AbstractAutoTagProcessor {

    @Autowired
    TagInstructionRepository tagInstructionRepository;

    @Autowired
    TagStructureServiceImpl tagStructureService;

    private List<Instruction> instructions;


    @Override
    public Long getProcessIdentifier() {
        return AutoTagProcessor.Type.Tag;
    }

    @Override
    protected List<Instruction> fillInstructions() {
            List<TagInstructionEntity> entities = tagInstructionRepository.findAll();
            for (TagInstructionEntity instruction : entities) {
                Set<Long> tagIdList = instruction.getMasterSearchTags();
                // get all group tags for these search tags
                HashMap<Long,List<Long>> searchGroups = tagStructureService.getSearchGroupsForTagIds(tagIdList);
                Set<Long> searchTags = new HashSet<>();
                searchTags.addAll(tagIdList);
                searchGroups.entrySet().stream().forEach(t-> searchTags.addAll(t.getValue()));
                instruction.setSearchTags(searchTags);
            }
            instructions = new ArrayList<>();
            instructions.addAll(entities);
        return instructions;
    }

    @Override
    protected Long processTagForInstruction(Instruction instr, AutoTagSubject subject) {
        TagInstructionEntity instruction = (TagInstructionEntity)instr;
        // determine if search term match exists
        List<Long> matches = instruction.getSearchTags().stream()
                .filter(st -> subject.getTagIdsForDish().contains(st))
                .collect(Collectors.toList());
        if (instruction.getInvert() && matches.isEmpty()) {
            List<Long> filterMatch = instruction.getInvertExclusions().stream()
                    .filter(t-> subject.getTagIdsForDish().contains(t))
                    .collect(Collectors.toList());
            if (filterMatch.isEmpty()) {
            return instruction.getAssignTagId();
            }
        }
        if (!instruction.getInvert() & matches.size() > 0) {
            return instruction.getAssignTagId();
        }
        return null;


    }

    protected List<Instruction> currentInstructions() {
        return instructions;
    }
}
