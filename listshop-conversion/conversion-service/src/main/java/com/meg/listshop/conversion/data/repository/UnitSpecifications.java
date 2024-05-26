package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.service.ConversionSpec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Objects;

public class UnitSpecifications {

    private UnitSpecifications() {
        // to hide public constructor
    }

    public static Specification<ConversionFactorEntity> matchingFromWithSpec(ConversionSpec spec) {
        return (root, query, criteriaBuilder) -> {
            Join<ConversionFactorEntity, UnitEntity> fromUnit = root.join("fromUnit");
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("type"), spec.getUnitType()));
            if (spec.getUnitSubtype() != null) {
                predicates.add(criteriaBuilder.equal(fromUnit.<String>get("subtype"), spec.getUnitSubtype()));
            }


            for (UnitFlavor flavor : spec.getFlavors()) {
                if (Objects.requireNonNull(flavor) == UnitFlavor.DishUnit) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                } else if (flavor == UnitFlavor.ListUnit) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                } else if (flavor == UnitFlavor.Liquid) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isLiquid"), true));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ConversionFactorEntity> matchingFromWithSpecGenericOnly(ConversionSpec spec) {
        return (root, query, criteriaBuilder) -> {
            Join<ConversionFactorEntity, UnitEntity> fromUnit = root.join("fromUnit");
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("type"), spec.getUnitType()));
            predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isTagSpecific"),false));
            if (spec.getUnitSubtype() != null) {
                predicates.add(criteriaBuilder.equal(fromUnit.<String>get("subtype"), spec.getUnitSubtype()));
            }


            for (UnitFlavor flavor : spec.getFlavors()) {
                if (Objects.requireNonNull(flavor) == UnitFlavor.DishUnit) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                } else if (flavor == UnitFlavor.ListUnit) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                } else if (flavor == UnitFlavor.Liquid) {
                    predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isLiquid"), true));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ConversionFactorEntity> matchingToWithSpec(ConversionSpec spec) {
        return (root, query, criteriaBuilder) -> {
            Join<ConversionFactorEntity, UnitEntity> fromUnit = root.join("toUnit");
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("type"), spec.getUnitType()));
            if (spec.getUnitSubtype() != null) {
            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("subtype"), spec.getUnitSubtype()));
            }

            for (UnitFlavor flavor : spec.getFlavors()) {
                switch (flavor) {
                    case DishUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                        continue;
                    case ListUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                        continue;
                    default:
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ConversionFactorEntity> matchingToWithSpecGenericOnly(ConversionSpec spec) {
        return (root, query, criteriaBuilder) -> {
            Join<ConversionFactorEntity, UnitEntity> fromUnit = root.join("toUnit");
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("type"), spec.getUnitType()));
            predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isTagSpecific"),false));
            if (spec.getUnitSubtype() != null) {
            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("subtype"), spec.getUnitSubtype()));
            }

            for (UnitFlavor flavor : spec.getFlavors()) {
                switch (flavor) {
                    case DishUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                        continue;
                    case ListUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                        continue;
                    default:
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ConversionFactorEntity> matchingFromConversionId(Long conversionId) {
        return (root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.<String>get("conversionId"), conversionId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
