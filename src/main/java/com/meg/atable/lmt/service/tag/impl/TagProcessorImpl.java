package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.data.entity.TagInstructionEntity;
import com.meg.atable.lmt.data.repository.TagInstructionRepository;
import com.meg.atable.lmt.service.Instruction;
import com.meg.atable.lmt.service.tag.AutoTagProcessor;
import com.meg.atable.lmt.service.tag.AutoTagSubject;
import com.meg.atable.lmt.service.tag.TagStructureService;
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
    TagStructureService tagStructureService;

    private List<Instruction> instructions;


    @Override
    public Long getProcessIdentifier() {
        return AutoTagProcessor.Type.Tag;
    }

    @Override
    public List<Instruction> fillInstructions() {
        List<TagInstructionEntity> entities = tagInstructionRepository.findAll();
        for (TagInstructionEntity instruction : entities) {
            Set<Long> tagIdList = instruction.getMasterSearchTags();
            // get all group tags for these search tags
            Map<Long, List<Long>> searchGroups = tagStructureService.getSearchGroupsForTagIds(tagIdList);
            Set<Long> searchTags = new HashSet<>();
            searchTags.addAll(tagIdList);
            searchGroups.entrySet().stream().forEach(t -> searchTags.addAll(t.getValue()));
            instruction.setSearchTags(searchTags);
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
        if (!instruction.getInvert() & !matches.isEmpty()) {
            return instruction.getAssignTagId();
        }
        return null;


    }

    protected List<Instruction> currentInstructions() {
        return instructions;
    }
}
