package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.repository.CustomTagRelationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public class CustomTagRelationRepositoryImpl implements CustomTagRelationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final String DESCENDANT_TAG_QUERY = "WITH RECURSIVE all_children AS ( " +
            "    select t.tag_id from tag t where tag_id = ?1 " +
            "    UNION " +
            "    select t.tag_id from all_children n " +
            "                                     join tag_relation tr on n.tag_id = tr.parent_tag_id " +
            "                                     join tag t on t.tag_id = tr.child_tag_id " +
            ") " +
            "SELECT * FROM all_children;";

    public List<Long> getTagWithDescendants(Long tagId) {
        return entityManager.createNativeQuery(DESCENDANT_TAG_QUERY)
                .setParameter(1, tagId)
                .getResultList();
    }

}
