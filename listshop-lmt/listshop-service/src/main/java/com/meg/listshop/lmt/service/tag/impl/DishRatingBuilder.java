package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.model.DishRatingInfo;
import com.meg.listshop.lmt.api.model.RatingInfo;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DishRatingBuilder {
    RatingStructureTree ratingStructure;
    private Long dishId;
    private String dishName;

    public DishRatingBuilder(RatingStructureTree ratingStructure) {
        this.ratingStructure = ratingStructure;
    }

    public DishRatingBuilder withDishId(Long dishId) {
        this.dishId = dishId;
        return this;
    }

    public DishRatingBuilder withDishName(String dishName) {
        this.dishName = dishName;
        return this;
    }
    public DishRatingInfo buildRatingInfo(List<TagInfoDTO> tagsForDish) {
        // gather rating info for existing dish tags
        HashMap<Long, RatingInfo> collectedRatingInfo = new HashMap<>();  //ratingId, RatingInfo

        // fill in missing with defaults
        List<RatingInfo> defaults = ratingStructure.getRatingDefaults();
        defaults.forEach( defaultInfo -> collectedRatingInfo.putIfAbsent(defaultInfo.getRatingTagId(), defaultInfo));

        // fill in with dish tags
        if (tagsForDish != null) {
            tagsForDish.stream()
                    .map(taginfo -> ratingStructure.getRatingInfo(taginfo.getParentId(), taginfo.getPower()))
                    .filter(Objects::nonNull)
                    .forEach( rating -> collectedRatingInfo.put(rating.getRatingTagId(), rating));
        }

        // sort rating info into order
        List<RatingInfo> ratingInfoList = collectedRatingInfo.values().stream()
                .sorted((ratinginfo1, ratinginfo2) -> ratinginfo1.getRatingTagLabel().compareTo(ratinginfo2.getRatingTagLabel()))
                .collect(Collectors.toList());

        DishRatingInfo dishRatingInfo =  new DishRatingInfo(dishId, dishName);
        dishRatingInfo.setRatings(new LinkedHashSet<>(ratingInfoList));
        return dishRatingInfo;
    }
}
