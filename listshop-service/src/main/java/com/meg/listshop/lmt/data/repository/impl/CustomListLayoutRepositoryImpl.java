package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.repository.CustomListLayoutRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public class CustomListLayoutRepositoryImpl implements CustomListLayoutRepository {

    private static final Logger logger = LogManager.getLogger(CustomListLayoutRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ListLayoutEntity> getFilledUserLayouts(Long userId) {
        logger.debug("Retrieving user layouts for user [%d]", userId);
        EntityGraph<?> graph = entityManager.createEntityGraph("graph.LayoutCategoriesItems");
        TypedQuery<ListLayoutEntity> q = entityManager.createQuery("SELECT l FROM ListLayoutEntity l WHERE l.userId = ?1", ListLayoutEntity.class);
        q.setParameter(1, userId);
        q.setHint("javax.persistence.fetchgraph", graph);

        return q.getResultList();
    }
}
