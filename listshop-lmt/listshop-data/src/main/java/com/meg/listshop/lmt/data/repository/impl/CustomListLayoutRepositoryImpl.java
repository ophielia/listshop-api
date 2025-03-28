package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.CustomListLayoutRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 21/10/2017.
 */
public class CustomListLayoutRepositoryImpl implements CustomListLayoutRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomListLayoutRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ListLayoutEntity fillLayout(Long userId, ListLayoutEntity layout) {
        logger.debug("Filling layout [{}]", layout.getId());
        // get layout
        List<Predicate> predicates = new ArrayList<Predicate>();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TagEntity> criteriaQuery = cb.createQuery(TagEntity.class);
        Root<TagEntity> root = criteriaQuery.from(TagEntity.class);
        Join<Object, Object> categoriesJoin2 = (Join<Object, Object>) root.fetch("categories");

        predicates.add(cb.equal(categoriesJoin2.get("layoutId"), layout.getId()));
        if (userId != null) {
            Predicate userIdIsNull = cb.isNull(root.get("userId"));
            Predicate userIdEquals = cb.equal(root.get("userId"), userId);
            predicates.add(cb.or(userIdIsNull, userIdEquals));
        } else {
            predicates.add(cb.isNull(root.get("userId")));
        }


        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TagEntity> query = entityManager.createQuery(criteriaQuery);
        List<TagEntity> layoutTags = query.getResultList();

        Map<Long, ListLayoutCategoryEntity> categories = new HashMap<>();
        layoutTags.forEach(tag -> {
            ListLayoutCategoryEntity category = tag.getCategories().get(0);
            Long categoryId = category.getId();
            if (!categories.containsKey(category.getId())) {
                entityManager.detach(category);
                category.setTags(new HashSet<>());
                categories.put(category.getId(), category);
            }
            category = categories.get(category.getId());
            categories.putIfAbsent(categoryId, category);
            categories.get(categoryId).getTags().add(tag);
        });
        layout.setCategories(new HashSet<>(categories.values()));
        return layout;
    }


    public Long getDefaultCategoryForSiblings(Set<Long> siblingIds) {

        Query query = entityManager.createNamedQuery("ListLayoutCategoryEntity.defaultCategoryForSiblings");
        query.setParameter("sibling_tags", siblingIds);
        query.setMaxResults(1);
        List<Long> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

    public Set<Long> getUserCategoriesForSiblings(Long userId, Set<Long> siblingIds) {

        Query query = entityManager.createNamedQuery("ListLayoutCategoryEntity.userCategoriesForSiblings");
        query.setParameter("sibling_ids", siblingIds);
        query.setParameter("user_id", userId);
        List<Long> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.stream()
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
