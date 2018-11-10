package com.meg.atable.lmt.service.tag;

import com.meg.atable.lmt.api.model.FatTag;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.entity.TagRelationEntity;
import com.meg.atable.lmt.data.entity.TagSearchGroupEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagStructureService {
    List<TagEntity> getBaseTagList(List<TagType> tagTypes);

    TagRelationEntity createRelation(TagEntity parentTag, TagEntity childTag);

    TagEntity assignTagToParent(TagEntity childTag, TagEntity newParentTag);

    List<TagEntity> getChildren(TagEntity parent);

    List<TagEntity> fillInRelationshipInfo(List<TagEntity> tags);

    boolean assignTagToTopLevel(TagEntity tag);

    List<TagEntity> getAscendantTags(TagEntity tag, Boolean searchSelectOnly);

    List<TagEntity> getDescendantTags(TagEntity tag, Boolean searchSelectOnly);

    TagEntity getParentTag(TagEntity tag);

    List<TagEntity> getSiblingTags(TagEntity afterChange);

    List<TagSearchGroupEntity> buildGroupAssignments(Long groupId, List<TagEntity> members);

    void deleteTagGroupsByGroupAndMember(List<Long> groupTagIds, List<Long> memberTagIds);

    Map<Long, List<Long>> getSearchGroupsForTagIds(Set<Long> allTags);

    Map<Long, TagSwapout> getTagSwapouts(List<Long> dishIds, List<String> tagListForSlot);

    List<Long> getSearchGroupIdsByMember(Long id);

    List<Long> getSearchMemberIdsByGroup(Long id);

    void createGroupsForMember(Long id, List<Long> toAdd);

    void removeGroupsForMember(Long id, List<Long> toDelete);

    void createMembersForGroup(Long id, List<Long> toAdd);

    void removeMembersForGroup(Long id, List<Long> toDelete);

    List<FatTag> getTagsWithChildren(List<TagType> tagTypes);
}
