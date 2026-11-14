/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.conversion.data.repository.impl;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.entity.ConversionBridgeFactorEntity;
import com.meg.listshop.conversion.data.entity.ConversionFactor;
import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.SimpleConversionFactor;
import com.meg.listshop.conversion.data.pojo.ConversionTargetType;
import com.meg.listshop.conversion.data.repository.CustomConversionFactorRepository;
import com.meg.listshop.conversion.data.repository.FactorCriteria;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CustomConversionFactorRepositoryImpl implements CustomConversionFactorRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomConversionFactorRepositoryImpl.class);
    private final MeterRegistry meterRegistry;

    @PersistenceContext
    EntityManager entityManager;


    private Cache<CacheKey, List<ConversionFactor>> cache;
    private Cache<CacheKey, List<ConversionFactor>> tagCache;
    private long maximumCacheSize;
    private long maximumTagCacheSize;

    @Autowired
    public CustomConversionFactorRepositoryImpl(
            MeterRegistry meterRegistry,
            @Value("${conversion.factor.cache.size:300}") long maximumCacheSize,
            @Value("${conversion.factor.tag.cache.size:300}") long maximumTagCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
        this.maximumTagCacheSize = maximumTagCacheSize;
        this.meterRegistry = meterRegistry;
        initializeCache();
    }

    private void initializeCache() {
        cache = Caffeine.newBuilder()
                .maximumSize(maximumCacheSize)
                .recordStats()
                .build();
        tagCache = Caffeine.newBuilder()
                .maximumSize(maximumTagCacheSize)
                .recordStats()
                .build();

        CaffeineCacheMetrics.monitor(
                meterRegistry,
                cache,
                "standardConversionFactors"
        );

        CaffeineCacheMetrics.monitor(
                meterRegistry,
                tagCache,
                "tagConversionFactors"
        );
    }

    @Override
    public List<ConversionFactor> findAllFactors(FactorCriteria criteria) {
        List<ConversionFactor> factors = findFactors(criteria);
        List<ConversionFactor> allFactors = new ArrayList<>(factors);
        allFactors.addAll(findInvertedFactors(criteria));
        return allFactors;
    }

    @Override
    public List<ConversionFactor> findFactors(FactorCriteria criteria) {
            List<ConversionFactor> factors = getCachedValue(criteria, false,this::doFindFactors);
            LOG.debug("cache: [hits{}][misses{}][size{}]", cache.stats().hitCount(), cache.stats().missCount(), cache.estimatedSize());
            LOG.debug("tagCache: [hits{}][misses{}][size{}]", tagCache.stats().hitCount(), tagCache.stats().missCount(), tagCache.estimatedSize());
            return factors;

    }

    private List<ConversionFactor> findInvertedFactors(FactorCriteria criteria) {
            return getCachedValue(criteria,true, this::doFindInvertedFactors);
    }

    private List<ConversionFactor> doFindFactors(FactorCriteria criteria) {
        LOG.debug("Finding conversion factors with criteria: {}", criteria);
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

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit, criteria);

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList().stream()
                .map(f -> (ConversionFactor) f)
                .toList();
    }

    private List<ConversionFactor> doFindInvertedFactors(FactorCriteria criteria) {
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

        List<Predicate> predicates = buildPredicatesFromCriteria(cb, root, fromUnit, toUnit, criteria);
        predicates.add(cb.not(cb.isTrue(fromUnit.get("oneWayConversion"))));
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList().stream()
                .map(f -> (ConversionFactor) f)
                .toList();
    }

    private List<ConversionFactor> getCachedValue(FactorCriteria criteria, boolean inverted, Function<FactorCriteria, List<ConversionFactor>> lookupMethod) {
        CacheKey key = CacheKey.fromCriteria(criteria, inverted);
        return getCacheForCriteria(criteria).get(key, k -> lookupMethod.apply(criteria));
    }

    private Cache<CacheKey, List<ConversionFactor>> getCacheForCriteria(FactorCriteria criteria) {
        if (criteria.getConversionId() == null) {
            return cache;
        } else {
            return tagCache;
        }
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
        if (criteria.getToUnitIds() != null) {
            predicates.add(toUnit.get("id").in(criteria.getToUnitIds()));
        }
        if (criteria.getToContext() != null) {
            if (criteria.getToContext() == ConversionTargetType.List) {
                predicates.add(cb.isTrue(toUnit.<Boolean>get("isListUnit")));
            } else {
                predicates.add(cb.isTrue(toUnit.<Boolean>get("isDishUnit")));
            }
        }
        if (criteria.getToDomain() != null) {
            predicates.add(cb.equal(toUnit.<String>get("type"), criteria.getToDomain()));
        }
        if (criteria.getToSize() != null) {
            predicates.add(cb.equal(root.<String>get("unitSize"), criteria.getToSize()));
        }
        if (criteria.isToDefaultUnit()) {
            predicates.add(cb.isTrue(toUnit.<Boolean>get("domainDefault")));
        }

        //MM clean this up to have only default size (not from and to)
        if (criteria.isToDefaultSize() || criteria.isFromDefaultSize()) {
            predicates.add(cb.isTrue(root.<Boolean>get("unitDefault")));
        }
        if (criteria.getFromContext() != null) {
            if (criteria.getFromContext() == ConversionTargetType.List) {
                predicates.add(cb.isTrue(fromUnit.<Boolean>get("isListUnit")));
            } else {
                predicates.add(cb.isTrue(fromUnit.<Boolean>get("isDishUnit")));
            }
        }
        if (criteria.getFromModifier() != null) {
            predicates.add(cb.equal(root.<Boolean>get("marker"), criteria.getFromModifier()));
        }
        if (criteria.getFromDomain() != null) {
            predicates.add(cb.equal(fromUnit.<String>get("type"), criteria.getFromDomain()));
        }
        if (criteria.getFromUnitType() != null) {
            predicates.add(cb.equal(fromUnit.<Boolean>get("type"), criteria.getFromUnitType()));
        }
        if (criteria.getFromUnitTypes() != null && !criteria.getFromUnitTypes().isEmpty()) {
            predicates.add(fromUnit.get("type").in(criteria.getFromUnitTypes()));
        }
        if (criteria.getFromExcludeDomain() != null) {
            String pattern = "%" + criteria.getFromExcludeDomain() + "%";
            predicates.add(
                    cb.or(
                            cb.isNull(fromUnit.get("excludedDomainList")),
                            cb.not(cb.like(fromUnit.get("excludedDomainList"), pattern))
                    )
            );
        }
        if (criteria.getFromSize() != null) {
            predicates.add(cb.equal(root.<String>get("unitSize"), criteria.getFromSize()));
        }

        if (criteria.getSizeOrDefault() != null) {
            predicates.add(cb.or(
                    cb.isTrue(root.<Boolean>get("unitDefault")),
                    cb.equal(root.<Boolean>get("unitSize"), criteria.getSizeOrDefault())
            ));
        }
        if (criteria.getMarkerOrNull() != null) {
            predicates.add(cb.or(
                    cb.isNull(root.get("marker")),
                    cb.equal(root.get("marker"), criteria.getMarkerOrNull())
            ));
        }
        if (criteria.isExactModifierMatch() && criteria.getFromModifier() == null) {
            predicates.add(cb.isNull(root.<Boolean>get("marker")));
        }
        if (criteria.getConversionId() != null) {
            predicates.add(cb.equal(root.<String>get("conversionId"), criteria.getConversionId()));
        } else {
            predicates.add(cb.isNull(root.<String>get("conversionId")));
        }
        if (criteria.isToNotOneWay()) {
            predicates.add(cb.not(cb.isTrue(toUnit.get("oneWayConversion"))));
        }
        if (criteria.isFromNotOneWay()) {
            predicates.add(cb.not(cb.isTrue(fromUnit.get("oneWayConversion"))));
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

    private record CacheKey(
            Long fromUnitId,
            Long toUnitId,
            String fromSize,
            String toSize,
            String toUnitIds,
            boolean toDefaultUnit,
            String fromModifier,
            UnitType fromDomain,
            UnitType toDomain,
            boolean fromDefaultSize,
            boolean toDefaultSize,
            UnitType fromUnitType,
            String fromUnitTypes,
            ConversionTargetType fromContext,
            ConversionTargetType toContext,
            UnitType fromExcludeDomain,
            boolean fromNotOneWay,
            boolean toNotOneWay,
            Long conversionId,
            boolean bridgeThroughMetric,
            boolean exactModifierMatch,
            String markerOrNull,
            String sizeOrDefault,
            boolean inverted
    ) {

        public static CacheKey fromCriteria(FactorCriteria criteria, boolean inverted) {
            String toUnitIds = listToOrderedString((Collection) criteria.getToUnitIds());
            String fromUnitTypes = listToOrderedString((Collection) criteria.getFromUnitTypes());
            Long unitId = criteria.getFromUnit() != null ? criteria.getFromUnit().getId() : null;
            return new CacheKey(
                    unitId,
                    criteria.getToUnitId(),
                    criteria.getFromSize(),
                    criteria.getToSize(),
                    toUnitIds,
                    criteria.isToDefaultUnit(),
                    criteria.getFromModifier(),
                    criteria.getFromDomain(),
                    criteria.getToDomain(),
                    criteria.isFromDefaultSize(),
                    criteria.isToDefaultSize(),
                    criteria.getFromUnitType(),
                    fromUnitTypes,
                    criteria.getFromContext(),
                    criteria.getToContext(),
                    criteria.getFromExcludeDomain(),
                    criteria.isFromNotOneWay(),
                    criteria.isToNotOneWay(),
                    criteria.getConversionId(),
                    criteria.bridgeThroughMetric(),
                    criteria.isExactModifierMatch(),
                    criteria.getMarkerOrNull(),
                    criteria.getSizeOrDefault(),
                    inverted);

        }

        private static String listToOrderedString(Collection<Object> collection) {
            if (collection == null) {
                return null;
            }
            return collection.stream()
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.joining(","));
        }
    }
}
