package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.model.FatTag;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagInstructionEntity;
import com.meg.listshop.lmt.service.Instruction;
import com.meg.listshop.lmt.service.tag.AutoTagSubject;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 08/12/2017.
 */
@Service
public class RatingTagProcessorImpl extends AbstractAutoTagProcessor {


    @Autowired
    TagStructureService tagStructureService;

    private List<Instruction> instructions = new ArrayList<>();


    @Override
    public Long getProcessIdentifier() {
        return Type.RATING;
    }

    @Override
    public List<Instruction> fillInstructions() {
        instructions = new ArrayList<>();

        // the instructions are calculated - not retrived form the database
        List<FatTag> ratingTagsWithChildren = tagStructureService.getTagsWithChildren(Collections.singletonList(TagType.Rating));

        for (FatTag parentTag : ratingTagsWithChildren) {
            // sort children
            List<TagEntity> sortedChildren = parentTag.getChildren()
                    .stream()
                    .sorted(Comparator.comparing(FatTag::getPower))
                    .map(FatTag::getTag)
                    .collect(Collectors.toList());
            Set<Long> sortedIdSet = parentTag.getChildren()
                    .stream()
                    .map(FatTag::getId)
                    .collect(Collectors.toSet());
            int defaultIndex = Long.valueOf(Math.round(sortedChildren.size() / 2)).intValue();
            if (defaultIndex == 0) {
                continue;
            }
            TagEntity assignTag = sortedChildren.get(defaultIndex);

            TagInstructionEntity instructionEntity = new TagInstructionEntity();
            instructionEntity.setSearchTags(sortedIdSet);
            instructionEntity.setInvert(true);
            instructionEntity.setAssignTagId(assignTag.getId());
            instructions.add(instructionEntity);
        }

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
            return instruction.getAssignTagId();
        }
        return null;
    }

    protected List<Instruction> currentInstructions() {
        return instructions;
    }
}
