package com.meg.listshop.lmt.data.repository;

import java.util.List;

public interface CustomTagRelationRepository {

    List<Long> getTagWithDescendants(Long tagId);

    List<Long> getTagWithAscendants(Long tagId);
}