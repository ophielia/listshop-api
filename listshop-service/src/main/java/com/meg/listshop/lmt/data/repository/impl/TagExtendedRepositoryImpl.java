package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.TagExtendedEntity;
import com.meg.listshop.lmt.data.repository.TagExtendedRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class TagExtendedRepositoryImpl implements TagExtendedRepositoryCustom {


    EntityManager em;

    @Autowired
    public TagExtendedRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<TagExtendedEntity> findTagsByCriteria(List<TagType> tagTypes, Boolean parentsOnly) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TagExtendedEntity> cq = cb.createQuery(TagExtendedEntity.class);

        Root<TagExtendedEntity> tagEntityRoot = cq.from(TagExtendedEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (parentsOnly != null) {
            predicates.add(cb.isTrue(tagEntityRoot.get("isParent")));
        }
        ParameterExpression<Collection> paramTagTypes = cb.parameter(Collection.class);
        if (tagTypes != null && !tagTypes.isEmpty()) {
            predicates.add(tagEntityRoot.get("tagType").in(paramTagTypes));
        }
        predicates.add(cb.isFalse(tagEntityRoot.get("toDelete")));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TagExtendedEntity> typedQuery = em.createQuery(cq);
        if (tagTypes != null && !tagTypes.isEmpty()) {
            typedQuery.setParameter(paramTagTypes, tagTypes);
        }


        return typedQuery.getResultList();

    }
}
