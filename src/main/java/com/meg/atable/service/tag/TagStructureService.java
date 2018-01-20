package com.meg.atable.service.tag;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagRelationEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    List<TagSearchGroupEntity> buildGroupAssignments(Long groupId, List<TagEntity> members);

    void deleteTagGroupsByGroupAndMember(List<Long> groupTagIds,List<Long> memberTagIds);
}
