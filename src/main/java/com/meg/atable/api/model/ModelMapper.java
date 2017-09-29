package com.meg.atable.api.model;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ModelMapper {
    public static Dish toModel(DishEntity dishEntity) {
        List<Tag> tags = toModel(dishEntity.getTags());
        return new Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .tags(tags)
                .userId(dishEntity.getUserId());
    }

    private ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    private static List<Tag> toModel(List<TagEntity> tagEntities) {
        List<Tag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toModel(entity));
        }
        return tags;
    }

    public static Tag toModel(TagEntity tagEntity) {
        return new Tag(tagEntity.getId())
                .name(tagEntity.getName())
                .description(tagEntity.getDescription());
    }

    public static TagExtended toExtendedModel(TagEntity tagEntity) {
        return new TagExtended(tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getDescription(),
                tagEntity.getParentId(),
                tagEntity.getChildrenIds());
    }
}
