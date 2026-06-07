/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.CategoryTagMapping;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LayoutCategoryDTO;
import com.meg.listshop.lmt.data.pojos.LayoutDTO;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.LayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
@Transactional
@Qualifier("V2LayoutService")
public class V2LayoutServiceImpl extends BaseLayoutServiceImpl implements LayoutService {
    private static final Logger LOG = LoggerFactory.getLogger(V2LayoutServiceImpl.class);

    @Autowired
    public V2LayoutServiceImpl(ListLayoutRepository listLayoutRepository, ListLayoutCategoryRepository categoryRepository, TagRepository tagRepository, UserService userService) {
        super(listLayoutRepository, categoryRepository, tagRepository, userService);
    }

    private static Map<String, ListLayoutCategoryEntity> layoutCategoriesToMap(ListLayoutEntity layout) {
        Map<String, ListLayoutCategoryEntity> categoryMap = new HashMap<>();
        if (layout == null) {
            return categoryMap;
        }
        layout.getCategories().forEach(c -> {
            String name = c.getName();
            categoryMap.put(name.trim().toLowerCase(), c);
        });
        return categoryMap;
    }

    @Override
    public List<ListLayoutEntity> getAllLayouts(Long userId) {

        List<ListLayoutEntity> layouts = new ArrayList<>();
        layouts.add(getFilledStandardLayout(userId));
        if (userId != null) {
            layouts.addAll(listLayoutRepository.getUserLayouts(userId));
        }

        return layouts;
    }

    @Override
    public LayoutDTO getStandardLayout(Long userId) {
        // get standard layout
        ListLayoutEntity standardLayout = listLayoutRepository.getStandardLayout();
        // create tag mapping dictionary
        Map<Long, CategoryTagMapping> tagMappingDictionary = new HashMap<>();
        // do user specific mapping
        fillUserSpecificMappings(userId, standardLayout.getId(), tagMappingDictionary);
        // do standard mappings
        fillStandardMappings(standardLayout.getId(), tagMappingDictionary);
        // convert to category list
        List<LayoutCategoryDTO> categoryList = convertToCategoryList(tagMappingDictionary, userId);

        // create LayoutDTO and return
        return new LayoutDTO(standardLayout.getId(), standardLayout.getName(), standardLayout.getDefault(), userId, categoryList);
    }

    private List<LayoutCategoryDTO> convertToCategoryList(Map<Long, CategoryTagMapping> tagMappingDictionary, Long userId) {
        //     retrieve standard categories, and make name to id dictionary
        Map<String, LayoutCategoryDTO> nameToCategory = categoryRepository.getStandardCategories().stream()
                .collect(Collectors.toMap(LayoutCategoryDTO::getCategoryName, category -> category));

        tagMappingDictionary.entrySet().stream()
                .forEach(entry -> {
                    String categoryName = entry.getValue().categoryName();
                    if (nameToCategory.containsKey(categoryName)) {
                        nameToCategory.get(categoryName).addTagMapping(entry.getValue());
                    }
                });

        return new ArrayList<>(nameToCategory.values().stream()
                .filter(c -> c.getTags().size() > 0)
                .toList());
    }

    private void fillStandardMappings(Long layoutId, Map<Long, CategoryTagMapping> tagMappingDictionary) {
        List<CategoryTagMapping> tagMappings = categoryRepository.getStandardTagMappings(layoutId);
        tagMappings.stream()
                .filter(t -> !tagMappingDictionary.containsKey(t.tagId()))
                .forEach(mapping -> {
                    tagMappingDictionary.put(mapping.tagId(), mapping);
                });
    }

    private void fillUserSpecificMappings(Long userId, Long layoutId, Map<Long, CategoryTagMapping> tagMappingDictionary) {
        if (userId == null) {
            return;
        }
        ListLayoutEntity userLayout = listLayoutRepository.getDefaultUserLayout(userId);

        if (userLayout != null) {
            // get user specific mappings
            List<CategoryTagMapping> categoryMappings = categoryRepository.getTagCategoryMappings(userId, layoutId);

            for (CategoryTagMapping mapping : categoryMappings) {
                tagMappingDictionary.put(mapping.tagId(), mapping);
            }
        }

        // get user tags
        List<CategoryTagMapping> userTagMappings = categoryRepository.getUserTagMappings(userId);
        userTagMappings.stream()
                .filter(t -> !tagMappingDictionary.containsKey(t.tagId()))
                .forEach(mapping -> {
                    tagMappingDictionary.put(mapping.tagId(), mapping);
                });
    }

    @Override
    public List<ListLayoutEntity> getAllLayoutsWithTag(Long userId, Long tagId) {

        List<ListLayoutEntity> layouts = new ArrayList<>();
        layouts.add(getFilledStandardLayout(userId, tagId));
        if (userId != null) {
            layouts.addAll(listLayoutRepository.getUserLayoutsWithTag(userId, tagId));
        }

        return layouts;
    }

    @Override
    public ListLayoutEntity getFilledStandardLayout(Long userId) {
        ListLayoutEntity standardLayout = getStandardLayout();

        return listLayoutRepository.fillLayout(userId, null,standardLayout);
    }

    @Override
    public List<LayoutCategoryDTO> getDefaultCategories() {

        // get default layout for user
        ListLayoutEntity userDefaultLayout = getStandardLayout();

        // return categories for this layout
        return getAvailableCategoriesForLayout(userDefaultLayout).stream()
                .map(LayoutCategoryDTO::new)
                .toList();
    }



    private List<ListLayoutCategoryEntity> getAvailableCategoriesForLayout(ListLayoutEntity layout) {
        ListLayoutEntity defaultLayout = getStandardLayout();
        Map<String, ListLayoutCategoryEntity> userCategoryMap = layoutCategoriesToMap(layout);
        Map<String, ListLayoutCategoryEntity> defaultCategoryMap = layoutCategoriesToMap(defaultLayout);

        Set<String> userSortedKeys = userCategoryMap.keySet().stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> defaultSortedKeys = defaultCategoryMap.keySet().stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        // produce sorted list of categories
        List<ListLayoutCategoryEntity> result = new ArrayList<>();
        userSortedKeys.forEach(key -> result.add(userCategoryMap.get(key)));
        defaultSortedKeys.stream()
                .filter(key -> !userSortedKeys.contains(key))
                .forEach(key -> result.add(defaultCategoryMap.get(key)));

        return result;
    }

    public void removeTagFromCategory(ListLayoutCategoryEntity categoryEntity, TagEntity tag) {
        Set<TagEntity> tags = categoryEntity.getTags();
        if (!tags.stream().anyMatch(t -> t.getId().equals(tag.getId()))) {
            return;
        }
        tags.remove(tag);
        tag.getCategories().remove(categoryEntity);
        categoryEntity.setTags(tags);
        categoryRepository.save(categoryEntity);
    }

    @Override
    public void moveTagToDefaultCategory(Long tagId, Long categoryId) {
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt = categoryRepository.findById(categoryId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutEntity listLayoutEntity = listLayoutRepository.findById(listLayoutEntityOpt.get().getLayoutId())
                .orElse(null);
        if (listLayoutEntity == null || listLayoutEntity.getUserId() != null
                || (listLayoutEntity.getDefault() != null && listLayoutEntity.getDefault() != true)) {
            // not a default category
            return;
        }

        Optional<TagEntity> tag = tagRepository.findById(tagId);
        if (!tag.isPresent() || tag.get().getUserId() != null) {
            return;
        }

        // get existing standard category
        ListLayoutCategoryEntity existing = categoryRepository.getStandardCategoryForTag(tagId);

        // remove from existing category
        if (existing != null) {
            removeTagFromCategory(existing, tag.get());
        }

        // add to new category
        ListLayoutCategoryEntity categoryEntity = listLayoutEntityOpt.get();
        Set<TagEntity> tags = categoryEntity.getTags();
        if (tags.stream().anyMatch(t -> t.getId().equals(tagId))) {
            return;
        }
        doAddCategory(categoryEntity, tag.get());

    }


    private void doAddCategory(ListLayoutCategoryEntity categoryEntity, TagEntity tag) {

        categoryEntity.getTags().add(tag);
        tag.getCategories().add(categoryEntity);

        categoryRepository.save(categoryEntity);
    }

    private ListLayoutEntity getFilledStandardLayout(Long userId, Long tagId) {
        ListLayoutEntity standardLayout = getStandardLayout();

        return listLayoutRepository.fillLayout(userId, tagId,standardLayout);
    }

}
