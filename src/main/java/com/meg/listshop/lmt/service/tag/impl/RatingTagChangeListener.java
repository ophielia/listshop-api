package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.service.tag.TagChangeListener;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class RatingTagChangeListener implements TagChangeListener {


    @Autowired
    private TagService tagService;

    @Autowired
    private TagStructureService tagStructureService;


    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }


    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        if (!TagType.Rating.equals(childTag.getTagType())) {
            return;
        }

        updateGroupsAndMembers(childTag);

    }

    @Override
    public void onTagUpdate(TagEntity beforeChange, TagEntity afterChange) {
        if (!TagType.Rating.equals(afterChange.getTagType())) {
            return;
        }
        // in this one , we need to know if the power changed
        if (beforeChange.getPower() != null &&
                afterChange.getPower() != null &&
                beforeChange.getPower().equals(afterChange.getPower())) {
            return;
        }

        updateGroupsAndMembers(afterChange);

    }

    @Override
    public void onTagAdd(TagEntity newTag) {
        // not used in this implementation
    }

    @Override
    public void onTagDelete(TagEntity deletedTag) {
// not used in this implementation
    }

    private void updateGroupsAndMembers(TagEntity tagEntity) {

        List<TagEntity> siblingGroups = tagStructureService.getSiblingTags(tagEntity);

        if (siblingGroups == null) {
            return;
        }
        // if the power changed
        // get existing groups
        List<Long> existingGroupIds = tagStructureService.getSearchGroupIdsByMember(tagEntity.getId());

        // get eligible groups - all siblings with power less than current tag
        List<Long> eligibleGroupIds = new ArrayList<>();
        if (siblingGroups != null) {
            siblingGroups.stream()
                    .filter(t -> t.getPower() != null && t.getPower() < tagEntity.getPower())
                    .map(TagEntity::getId)
                    .collect(Collectors.toList());
        }

        adjustGroups(tagEntity, existingGroupIds, eligibleGroupIds);

        // get existing members
        List<Long> existingMemberIds = tagStructureService.getSearchMemberIdsByGroup(tagEntity.getId());

        // get eligible members
        List<Long> eligibleMemberIds = siblingGroups.stream()
                .filter(t -> t.getPower() != null && t.getPower() > tagEntity.getPower())
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        eligibleMemberIds.add(tagEntity.getId());

        adjustMembers(tagEntity, existingMemberIds, eligibleMemberIds);


    }

    private void adjustGroups(TagEntity tag, List<Long> existingGroupIds, List<Long> eligibleGroupIds) {
        List<Long> toDelete = new ArrayList<>();
        List<Long> toAdd = new ArrayList<>();
        // go through eligible groups, seeing what exists, and what doesn't
        for (Long eligible : eligibleGroupIds) {
            if (existingGroupIds.contains(eligible)) {
                // this already exists.  doesn't need to be added
                // removing from existing, so only what needs to be deleted remains
                // in existing
                existingGroupIds.remove(eligible);
            } else {
                toAdd.add(eligible);
            }
        }
        toDelete.addAll(existingGroupIds);
        if (!toAdd.isEmpty()) {
            tagStructureService.createGroupsForMember(tag.getId(), toAdd);
        }
        if (!toDelete.isEmpty()) {
            tagStructureService.removeGroupsForMember(tag.getId(), toDelete);
        }
    }

    private void adjustMembers(TagEntity tag, List<Long> existingMemberIds, List<Long> eligibleMemberIds) {
        List<Long> toDelete = new ArrayList<>();
        List<Long> toAdd = new ArrayList<>();
        // go through eligible groups, seeing what exists, and what doesn't
        for (Long eligible : eligibleMemberIds) {
            if (existingMemberIds.contains(eligible)) {
                // this already exists.  doesn't need to be added
                // removing from existing, so only what needs to be deleted remains
                // in existing
                existingMemberIds.remove(eligible);
            } else {
                toAdd.add(eligible);
            }
        }
        toDelete.addAll(existingMemberIds);
        if (!toAdd.isEmpty()) {
            tagStructureService.createMembersForGroup(tag.getId(), toAdd);
        }
        if (!toDelete.isEmpty()) {
            tagStructureService.removeMembersForGroup(tag.getId(), toDelete);
        }
    }

}
