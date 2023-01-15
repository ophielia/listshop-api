package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.api.model.RatingInfo;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;

import java.util.*;
import java.util.stream.Collectors;

class RatingStructureTree {

    HashMap<Long, RatingStructure> ratings;

    public RatingStructureTree(List<TagInfoDTO> ratings) {
        buildRatingsFromTags(ratings);
    }

    private void buildRatingsFromTags(List<TagInfoDTO> ratings) {
        // make hash of parent id to list of children
        HashMap<Long, TagInfoDTO> containedInInput = new HashMap<>();
        HashMap<Long, List<TagInfoDTO>> parentToChildren = new HashMap<>();
        ratings.forEach(t -> {
            containedInInput.put(t.getTagId(), t);
            parentToChildren.putIfAbsent(t.getParentId(), new ArrayList<>());
            (parentToChildren.get(t.getParentId())).add(t);
        });

        // pull parent ids (those children who have a parent not in ratings)
        this.ratings = new HashMap<>();
        parentToChildren.keySet().stream()
                .filter(id -> !containedInInput.containsKey(id)) // not in ratings
                .flatMap(id -> parentToChildren.get(id).stream())
                .filter(tagInfo -> parentToChildren.containsKey(tagInfo.getTagId()))  // where we have children tags
                .map(TagInfoDTO::getTagId)
                .map(id -> new RatingStructure(containedInInput.get(id), parentToChildren.get(id)))
                .forEach(ratingWithTags ->this.ratings.put(ratingWithTags.getRatingId(), ratingWithTags));
    }


    public Long getDefaultTagIdForRating(Long ratingId) {
        RatingStructure ratingWithTags = ratings.get(ratingId);

        return ratingWithTags.getDefaultTag().getTagId();

    }

    public List<RatingInfo> getRatingDefaults() {
        return ratings.values().stream()
                .map(RatingStructure::getDefaultRatingInfo)
                .collect(Collectors.toList());

    }

    public Set<Long> getAllDefaultTagIds() {
        return ratings.values().stream()
                .map(RatingStructure::getDefaultTag)
                .map(TagInfoDTO::getTagId)
                .collect(Collectors.toSet());
    }

    public RatingInfo getRatingInfo(Long ratingId, Double power) {
        RatingStructure rating = ratings.get(ratingId);
        return rating.getRatingInfoForPower(power);
    }

    private class RatingStructure {

        private Long ratingId;
        private String ratingTagLabel;
        private Integer maxPower;
        private Integer defaultPower;

        private List<TagInfoDTO> childrenTags = new ArrayList<>();

        public RatingStructure(TagInfoDTO parent, List<TagInfoDTO> children) {
            // set values from parent
            ratingId = parent.getTagId();
            ratingTagLabel = parent.getName();

            // set values from children
            maxPower = children.size();
            var defaultPowerCeiling = Double.valueOf(Math.ceil((maxPower) / 2.0));
            defaultPower = defaultPowerCeiling.intValue();

            // set children in RatingWithTags
            children.sort(Comparator.comparing(TagInfoDTO::getPower));
            childrenTags = children;
        }

        public TagInfoDTO getDefaultTag() {

            return childrenTags.get(defaultPower);
        }

        public RatingInfo getDefaultRatingInfo() {
            return getRatingInfoForPower(defaultPower);
        }

        public Long getRatingId() {
            return ratingId;
        }

        private RatingInfo getRatingInfoForPower(Integer power) {
            RatingInfo ratingInfo = new RatingInfo(ratingId, ratingTagLabel);
            ratingInfo.setPower(power);
            ratingInfo.setMaxPower(maxPower);
            return ratingInfo;
        }

        private RatingInfo getRatingInfoForPower(Double power) {
            Integer powerInt = power.intValue();
            return getRatingInfoForPower(powerInt);
        }
    }
}
