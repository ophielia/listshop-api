package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.TagRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public List<TagEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean assignSelect, Boolean searchSelect) {


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TagEntity> cq = cb.createQuery(TagEntity.class);

        Root<TagEntity> tagEntityRoot = cq.from(TagEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Boolean> paramAssignSelect = cb.parameter(Boolean.class);
        if (assignSelect != null) {
            predicates.add(cb.equal(tagEntityRoot.get("assignSelect"), paramAssignSelect));
        }
        ParameterExpression<Boolean> paramSearchSelect = cb.parameter(Boolean.class);
        if (searchSelect != null) {
            predicates.add(cb.equal(tagEntityRoot.get("searchSelect"), paramSearchSelect));
        }
        ParameterExpression<Collection> paramTagTypes = cb.parameter(Collection.class);
        if (tagTypes != null && !tagTypes.isEmpty()) {
            predicates.add(tagEntityRoot.get("tagType").in(paramTagTypes));
        }
        predicates.add(cb.isFalse(tagEntityRoot.get("toDelete")));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TagEntity> typedQuery = em.createQuery(cq);
        if (assignSelect != null) {
            typedQuery.setParameter(paramAssignSelect, assignSelect);
        }
        if (searchSelect != null) {
            typedQuery.setParameter(paramSearchSelect, searchSelect);
        }
        if (tagTypes != null && !tagTypes.isEmpty()) {
            typedQuery.setParameter(paramTagTypes, tagTypes);
        }


        return typedQuery.getResultList();
    }
}
