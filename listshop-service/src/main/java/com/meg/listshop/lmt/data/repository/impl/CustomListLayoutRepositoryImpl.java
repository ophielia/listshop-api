package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.CustomListLayoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public class CustomListLayoutRepositoryImpl implements CustomListLayoutRepository {

    private static final Logger  logger = LoggerFactory.getLogger(CustomListLayoutRepositoryImpl.class);

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

    @Override
    public ListLayoutEntity getFilledDefaultLayout(Long userId) {
        logger.debug("Retrieving default layout");

        // get layout
        EntityGraph<?> graph = entityManager.createEntityGraph("graph.LayoutCategoriesItems");
        List<Predicate> predicates = new ArrayList<Predicate>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ListLayoutEntity> criteriaQuery = cb.createQuery(ListLayoutEntity.class);
        Root<ListLayoutEntity> root = criteriaQuery.from(ListLayoutEntity.class);
        SetJoin<ListLayoutEntity, ListLayoutCategoryEntity> categoryJoin = root.joinSet("categories");
        SetJoin<ListLayoutCategoryEntity, TagEntity> tagJoin = categoryJoin.joinSet("tags");

        predicates.add(cb.isNull(root.get("userId")));
        predicates.add(cb.isTrue(root.get("isDefault")));
        if (userId != null) {
            Predicate userIdIsNull = cb.isNull(tagJoin.get("userId"));
            Predicate userIdEquals = cb.equal(tagJoin.get("userId"),userId);
            predicates.add(cb.or(userIdIsNull,userIdEquals));
        } else {
            predicates.add(cb.isNull(tagJoin.get("userId")));
        }


        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<ListLayoutEntity> query = entityManager.createQuery(criteriaQuery);
        query.setHint("javax.persistence.fetchgraph", graph);
        List<ListLayoutEntity> returnList = query.getResultList();

        if (returnList.isEmpty()) {
            return null;
        } else {
            return returnList.get(0);
        }

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

    private Set<ListLayoutCategoryEntity> getCategoriesForDefault(Long layoutId) {
        logger.debug("Retrieving categories for default layout [%d]", layoutId);

       // EntityGraph<?> graph = entityManager.createEntityGraph("graph.CategoryTags");
        TypedQuery<ListLayoutCategoryEntity> q = entityManager.createQuery("SELECT l from ListLayoutCategoryEntity  l " +
                "JOIN FETCH TagEntity t  " +
                "WHERE t.userId is null " +
                "AND l.layoutId = ?1", ListLayoutCategoryEntity.class);
        q.setParameter(1, layoutId);
    //    q.setHint("javax.persistence.fetchgraph", graph);

        return new HashSet<>(q.getResultList());

        // EntityGraph<?> graph = entityManager.createEntityGraph("graph.CategoryTags");
   /*
         TypedQuery<ListLayoutEntity> q = entityManager.createQuery("SELECT l FROM ListLayoutCategoryEntity l " +
                "LEFT JOIN FETCH TagEntity t ON t.categories = l " +
                "WHERE t.userId is null " +
                "AND l.layoutId = ?1", ListLayoutEntity.class);
        q.setParameter(1, userId);
        q.setHint("javax.persistence.fetchgraph", graph);

        return q.getResultList();
   */
    }
}
