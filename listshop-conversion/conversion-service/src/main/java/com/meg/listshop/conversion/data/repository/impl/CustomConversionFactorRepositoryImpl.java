/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository.impl;


import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionBridgeFactorEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.repository.CustomConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import com.meg.listshop.conversion.service.ConversionSpec;
import com.meg.listshop.conversion.service.factors.AbstractConversionFactorSource;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;


public class CustomConversionFactorRepositoryImpl implements CustomConversionFactorRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomConversionFactorRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    public List<ConversionFactor> findFactorsForSourceAndTarget(ConversionSpec source, ConversionSpec target) {
        return new ArrayList<>();
    }

    public List<ConversionFactor> findFactorsForCriteria(ConversionSpec source, ConversionSpec target) {
        return new ArrayList<>();
    }

    @Override
    public List<ConversionFactor> findAllFactors(FactorCriteria criteria) {
        List<ConversionFactor> factors = findFactors(criteria);
        List<ConversionFactor> allFactors = new ArrayList<>(factors);
        allFactors.addAll(findInvertedFactors(criteria));
        return allFactors;
    }

    private List<ConversionFactor> findInvertedFactors(FactorCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SimpleConversionFactor> query = cb.createQuery(SimpleConversionFactor.class);
        Root<? extends ConversionFactor> root = getRootForCriteria(query, criteria);
        Join<? extends ConversionFactor, UnitEntity> fromUnit = root.join("toUnit");
        Join<? extends ConversionFactor, UnitEntity> toUnit = root.join("fromUnit");

        Expression<Number> invertedFactor = cb.quot(cb.literal(1.0), root.get("factor"));
        query.select(cb.construct(SimpleConversionFactor.class,
                invertedFactor,
                root.get("fromUnit"),
                root.get("toUnit"),
                root.get("marker"),
                root.get("unitSize"),
                root.get("unitDefault")
        ));

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit,criteria);
        predicates.add(cb.not(cb.isTrue(fromUnit.get("oneWayConversion"))));
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList().stream()
                .map(f -> (ConversionFactor) f)
                .toList();
    }

    @Override
    public List<ConversionFactor> findFactors(FactorCriteria criteria) {
        LOG.info("Finding conversion factors with criteria: {}", criteria);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SimpleConversionFactor> query = cb.createQuery(SimpleConversionFactor.class);
        Root<? extends ConversionFactor> root = getRootForCriteria(query, criteria);
        Join<? extends ConversionFactor, UnitEntity> fromUnit = root.join("fromUnit");
        Join<? extends ConversionFactor, UnitEntity> toUnit = root.join("toUnit");

        query.select(cb.construct(SimpleConversionFactor.class,
                root.get("factor"),
                root.get("toUnit"),
                root.get("fromUnit"),
                root.get("marker"),
                root.get("unitSize"),
                root.get("unitDefault")
        ));

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit,criteria);

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList().stream()
                .map(f -> (ConversionFactor) f)
                .toList();
    }

    private List<Predicate> buildPredicatesFromCriteria(CriteriaBuilder cb,
                                                        Root<? extends ConversionFactor> root,
                                                        Join<? extends ConversionFactor, UnitEntity> fromUnit,
                                                        Join<? extends ConversionFactor, UnitEntity> toUnit,
                                                                FactorCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getFromUnit() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("id"), criteria.getFromUnit().getId()));
        }

        if (criteria.getToUnitId() != null) {
            predicates.add(cb.equal(toUnit.<String>get("id"), criteria.getToUnitId()));
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
        if (criteria.getConversionId() != null) {
            predicates.add(cb.equal(root.<String>get("conversionId"), criteria.getConversionId()));
        } else {
            predicates.add(cb.isNull(root.<String>get("conversionId")));
        }

        if (criteria.isTargetDefaultUnit()) {
            predicates.add(cb.isTrue(toUnit.<Boolean>get("domainDefault")));
        }
        return predicates;
    }

    private Root<? extends ConversionFactor> getRootForCriteria(CriteriaQuery<SimpleConversionFactor> query, FactorCriteria criteria) {
        if (criteria.bridgeThroughMetric()) {
            return query.from(ConversionBridgeFactorEntity.class);
        } else {
            return query.from(ConversionFactorEntity.class);
        }
    }
}
