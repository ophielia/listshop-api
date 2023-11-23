package com.meg.listshop.conversion.data.repository;

import com.meg.listshop.conversion.data.entity.ConversionFactorEntity;
import com.meg.listshop.conversion.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.UnitFlavor;
import com.meg.listshop.conversion.service.ConversionSpec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

public class UnitSpecifications {

    public static Specification<ConversionFactorEntity> matchingFromWithSpec(ConversionSpec spec) {
        return (root, query, criteriaBuilder) -> {
            Join<ConversionFactorEntity, UnitEntity> fromUnit = root.join("fromUnit");
            ArrayList<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(fromUnit.<String>get("type"), spec.getUnitType()));

            for (UnitFlavor flavor : spec.getFlavors()) {
                switch (flavor) {
                    case Weight:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isWeight"), true));
                        continue;
                    case Volume:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isVolume"), true));
                        continue;
                    case DishUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                        continue;
                    case ListUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                        continue;
                    case Liquid:
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

            for (UnitFlavor flavor : spec.getFlavors()) {
                switch (flavor) {
                    case Weight:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isWeight"), true));
                        continue;
                    case Volume:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isVolume"), true));
                        continue;
                    case DishUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isDishUnit"), true));
                        continue;
                    case ListUnit:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isListUnit"), true));
                        continue;
                    case Liquid:
                        predicates.add(criteriaBuilder.equal(fromUnit.<Boolean>get("isLiquid"), true));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}
