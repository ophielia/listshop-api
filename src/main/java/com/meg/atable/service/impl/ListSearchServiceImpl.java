package com.meg.atable.service.impl;

import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.ListSearchService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class ListSearchServiceImpl implements ListSearchService {

    @PersistenceContext
    private EntityManager entityManager;


    public Map<Long, Long> getTagToCategoryMap(Long listLayoutId, List<TagEntity> tagEntities) {
        List<Long> tagIds = tagEntities.stream().map(TagEntity::getId).collect(Collectors.toList());

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> q = builder.createTupleQuery();
        Root<ListLayoutCategoryEntity> c = q.from(ListLayoutCategoryEntity.class);
        Join<ListLayoutCategoryEntity, TagEntity> t = c.join("tags");
        q.multiselect(t.get("tag_id"), c.get("categoryId"));

        List<Predicate> predicates = new ArrayList<>();
        Expression<String> exp = t.get("tag_id");
        Predicate predicate = exp.in(tagIds);
        predicates.add(predicate);
        ParameterExpression<Long> p = builder.parameter(Long.class);
        Predicate layoutId = builder.equal(c.get("layoutId"), p);
        predicates.add(layoutId);
        q.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Tuple> query = entityManager.createQuery(q);
        query.setParameter(p, listLayoutId);
        List<Tuple> results = query.getResultList();

        Map<Long, Long> lookupResult = new HashMap<>();
        for (Tuple tp : results) {
            Long tagId = (Long) tp.get(0);
            Long categoryId = (Long) tp.get(1);
            lookupResult.put(tagId, categoryId);
        }

        return lookupResult;
    }


}
