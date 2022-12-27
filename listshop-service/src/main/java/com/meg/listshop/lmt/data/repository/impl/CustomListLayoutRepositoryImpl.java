package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.repository.CustomListLayoutRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    public Long getDefaultCategoryForSiblings(Set<Long> siblingIds) {

        Query query = entityManager.createNamedQuery("ListLayoutCategoryEntity.defaultCategoryForSiblings");
        query.setParameter("sibling_tags", siblingIds);
        query.setMaxResults(1);
        List<BigInteger> resultList = query.getResultList();
        if (!resultList.isEmpty() ) {
            return resultList.get(0).longValue();
        }
        return null;
    }

    public Set<Long> getUserCategoriesForSiblings(Long userId, Set<Long> siblingIds) {

        Query query = entityManager.createNamedQuery("ListLayoutCategoryEntity.userCategoriesForSiblings");
        query.setParameter("sibling_ids", siblingIds);
        query.setParameter("user_id", userId);
        List<BigInteger> resultList = query.getResultList();
        if (!resultList.isEmpty() ) {
            return resultList.stream()
                    .map(BigInteger::longValue)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
