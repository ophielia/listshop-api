/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository.impl;


import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.*;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.data.repository.TagFactorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TagFactorRepositoryImpl implements TagFactorRepository {
    private static final Logger LOG = LoggerFactory.getLogger(TagFactorRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<ConversionUnitFactorEntity> findAllFactors(FactorCriteria criteria) {
        List<ConversionUnitFactorEntity> factors = findFactors(criteria);
        List<ConversionUnitFactorEntity> allFactors = new ArrayList<>(factors);
        allFactors.addAll(findInvertedFactors(criteria));
        return allFactors;
    }


    private List<ConversionUnitFactorEntity> findFactors(FactorCriteria criteria) {
        LOG.info("Finding unit conversion factors with criteria: {}", criteria);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ConversionUnitFactorEntity> query = cb.createQuery(ConversionUnitFactorEntity.class);
        Root<ConversionUnitFactorEntity> root = query.from(ConversionUnitFactorEntity.class);
        Join<ConversionUnitFactorEntity, UnitEntity> fromUnit = root.join("fromUnit");
        Join<ConversionUnitFactorEntity, UnitEntity> toUnit = root.join("toUnit");

        query.select(cb.construct(ConversionUnitFactorEntity.class,
                root.get("factorId"),
                root.get("factor"),
                root.get("toUnit"),
                root.get("fromUnit"),
                root.get("conversionId"),
                root.get("fromMarker"),
                root.get("fromUnitSize"),
                root.get("fromUnitDefault"),
                root.get("toMarker"),
                root.get("toUnitSize"),
                root.get("toUnitDefault")
        ));

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit,criteria);

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    private List<ConversionUnitFactorEntity> findInvertedFactors(FactorCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ConversionUnitFactorEntity> query = cb.createQuery(ConversionUnitFactorEntity.class);
        Root<ConversionUnitFactorEntity> root = query.from(ConversionUnitFactorEntity.class);
        Join<ConversionUnitFactorEntity, UnitEntity> fromUnit = root.join("toUnit");
        Join<ConversionUnitFactorEntity, UnitEntity> toUnit = root.join("fromUnit");

        Expression<Number> invertedFactor = cb.quot(cb.literal(1.0), root.get("factor"));
        query.select(cb.construct(ConversionUnitFactorEntity.class,
                root.get("factorId"),
                invertedFactor,
                root.get("toUnit"),
                root.get("fromUnit"),
                root.get("conversionId"),
                root.get("fromMarker"),
                root.get("fromUnitSize"),
                root.get("fromUnitDefault"),
                root.get("toMarker"),
                root.get("toUnitSize"),
                root.get("toUnitDefault")
        ));

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit,criteria);
        predicates.add(cb.not(cb.isTrue(fromUnit.get("oneWayConversion"))));
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Predicate> buildPredicatesFromCriteria(CriteriaBuilder cb,
                                                        Root<ConversionUnitFactorEntity> root,
                                                        Join<ConversionUnitFactorEntity, UnitEntity> fromUnit,
                                                        Join<ConversionUnitFactorEntity, UnitEntity> toUnit,
                                                                FactorCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getConversionId() != null) {
            predicates.add(cb.equal(root.<String>get("conversionId"), criteria.getConversionId()));
        } else {
            predicates.add(cb.isNull(root.<String>get("conversionId")));
        }


        if (criteria.getFromUnit() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("id"), criteria.getFromUnit().getId()));
        }

        if (criteria.getFromSize() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("fromUnitSize"), criteria.getFromSize()));
        }

        if (criteria.getFromModifier() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("fromMarker"), criteria.getFromModifier()));
        }

        if (criteria.getToUnitId() != null) {
            predicates.add(cb.equal(toUnit.<String>get("id"), criteria.getToUnitId()));
        }
        if (criteria.getToSize() != null) {
            predicates.add(cb.equal(root.<String>get("toUnitSize"), criteria.getToSize()));
        }

        if (criteria.getToModifier() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("toMarker"), criteria.getToModifier()));
        }

        if (criteria.isToDefaultUnit()) {
            predicates.add(cb.isTrue(toUnit.<Boolean>get("domainDefault")));
        }
        if (criteria.isToDefaultUnit()) {
            predicates.add(cb.isTrue(root.<Boolean>get("toUnitDefault")));
        }
        if (criteria.isToDefaultSize()) {
            predicates.add(cb.isTrue(root.<Boolean>get("toUnitDefault")));
        }
        if (criteria.getToContext() != null) {
            if (criteria.getToContext() == ConversionTargetType.List ) {
                predicates.add(cb.isTrue(toUnit.<Boolean>get("isListUnit")));
            } else {
                predicates.add(cb.isTrue(toUnit.<Boolean>get("isDishUnit")));
            }
        }
        if (criteria.getToDomain() != null) {
            predicates.add(cb.equal(toUnit.<String>get("type"), criteria.getToDomain()));
        }
        return predicates;
    }


}
