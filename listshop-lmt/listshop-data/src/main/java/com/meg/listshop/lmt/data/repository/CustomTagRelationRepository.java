package com.meg.listshop.lmt.data.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomTagRelationRepository {

    List<Long> getTagWithDescendants(Long tagId);

    List<Long> getTagWithAscendants(Long tagId);

    Map<Long, List<Long>> getDescendantMap(Set<Long> tagIds, Long userId);

    Map<Long, List<Long>> getRatingsWithSiblingsByPower(List<Long> tagIds, boolean powerBelow, Long userId);
}