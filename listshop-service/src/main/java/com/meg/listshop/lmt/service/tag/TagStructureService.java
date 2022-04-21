package com.meg.listshop.lmt.service.tag;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagRelationEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface TagStructureService {
    TagRelationEntity createRelation(TagEntity parentTag, TagEntity childTag);

    TagEntity assignTagToParent(TagEntity childTag, TagEntity newParentTag);

    List<TagEntity> getChildren(TagEntity parent);

    List<TagEntity> fillInRelationshipInfo(List<TagEntity> tags);

    boolean assignTagToTopLevel(TagEntity tag);

    List<TagEntity> getAscendantTags(TagEntity tag);

    List<TagEntity> getDescendantTags(TagEntity tag);

    Set<Long> getDescendantsTagIds(Set<Long> tagIdList);

    Set<Long> getDescendantsTagIds(Long tagId);

    TagEntity getParentTag(TagEntity tag);

    List<TagEntity> getSiblingTags(TagEntity afterChange);

    Map<Long, List<Long>> getSearchGroupsForTagIds(Set<Long> allTags);


}
