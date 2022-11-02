package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.ListSearchService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListLayoutServiceImpl implements ListLayoutService {


    private final ListLayoutCategoryRepository listLayoutCategoryRepository;
    private final ListLayoutRepository listLayoutRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ListSearchService listSearchService;


    @Autowired
    public ListLayoutServiceImpl(ListLayoutCategoryRepository listLayoutCategoryRepository,
                                 ListLayoutRepository listLayoutRepository,
                                 TagRepository tagRepository,
                                 TagService tagService,
                                 ListSearchService listSearchService) {
        this.listLayoutCategoryRepository = listLayoutCategoryRepository;
        this.listLayoutRepository = listLayoutRepository;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
        this.listSearchService = listSearchService;
    }


    @Override
    public ListLayoutEntity getDefaultListLayout() {
        //MM layout
        //ListLayoutType layoutType = shoppingListProperties.getDefaultListLayoutType();
        //ListLayoutType layoutType = ListLayoutType.All;

        List<ListLayoutEntity> listLayoutEntity = null;
        //listLayoutRepository.findByLayoutType(layoutType);

        if (listLayoutEntity == null || listLayoutEntity.isEmpty()) {
            return null;
        }
        return listLayoutEntity.get(0);
    }

    @Override
    public ListLayoutCategoryEntity getDefaultListCategory() {
        //MM layout
        // get default list layout first
        //ListLayoutType layoutType = ListLayoutType.All;
        //ListLayoutType layoutType = shoppingListProperties.getDefaultListLayoutType();

        return null;//getDefaultCategoryForLayoutType(layoutType);
    }

    @Override
    public ListLayoutEntity getListLayoutById(Long listLayoutId) {

        Optional<ListLayoutEntity> listLayoutEntityOpt = listLayoutRepository.findById(listLayoutId);
        return listLayoutEntityOpt.orElse(null);
    }

    @Override
    public List<TagEntity> getTagsForLayoutCategory(Long layoutCategoryId) {
        return tagRepository.getTagsForLayoutCategory(layoutCategoryId);
    }

    @Override
    public List<ListLayoutCategoryEntity> getCategoriesForTag(TagEntity tag) {
        return listLayoutCategoryRepository.findByTagsContains(tag);
    }

    @Override
    public void addTagsToCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList) {
        // get  category
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt = listLayoutCategoryRepository.findById(layoutCategoryId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutCategoryEntity categoryEntity = listLayoutEntityOpt.get();
        // assure list owns category
        if (!categoryEntity.getLayoutId().equals(listLayoutId)) {
            return;
        }

        // get tags for list
        List<TagEntity> tagCategories = tagRepository.getTagsForLayoutCategory(layoutCategoryId);
        List<Long> tagIdsToAdd;

        // create filtered list - checking for tags already in list
        List<Long> alreadyInList = tagCategories.stream()
                .filter(t -> tagIdList.contains(t.getId()))
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        // if any tags already in list, filter from add list
        if (!alreadyInList.isEmpty()) {
            tagIdsToAdd = tagIdList.stream()
                    .filter(i -> !alreadyInList.contains(i))
                    .collect(Collectors.toList());
        } else {
            tagIdsToAdd = tagIdList;
        }

        // set tags in category
        List<TagEntity> tagsToAdd = tagRepository.findAllById(tagIdsToAdd);
        for (TagEntity updateTag : tagsToAdd) {
            updateTag.setCategoryUpdatedOn(new Date());
        }
        tagCategories.addAll(tagsToAdd);
        categoryEntity.setTags(tagCategories);

        // save category
        listLayoutCategoryRepository.save(categoryEntity);
    }

    @Override
    public void addTagToCategory(Long layoutCategoryId, TagEntity tag) {
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt = listLayoutCategoryRepository.findById(layoutCategoryId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutCategoryEntity categoryEntity = listLayoutEntityOpt.get();
        List<TagEntity> tags = categoryEntity.getTags();
        if (tags.stream().anyMatch(t -> t.getId().equals(tag.getId()))) {
            return;
        }
        tags.add(tag);
        tag.getCategories().add(categoryEntity);
        categoryEntity.setTags(tags);
        listLayoutCategoryRepository.save(categoryEntity);
    }


    @Override
    public List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId) {
        return listLayoutCategoryRepository.findByLayoutIdEquals(layoutId);
    }


    @Override
    public ListLayoutCategoryEntity getLayoutCategoryForTag(Long listLayoutId, Long tagId) {
        //MM see about a lazy catagory load here - category without tags
        TagEntity tagEntity = tagService.getTagById(tagId);
        List<TagEntity> tagList = Collections.singletonList(tagEntity);

        Map<Long, Long> dictionary = listSearchService.getTagToCategoryMap(listLayoutId, tagList);
        if (!dictionary.containsKey(tagId)) {
            return null;
        }

        Long categoryId = dictionary.get(tagId);
        return listLayoutCategoryRepository.getOne(categoryId);

    }

    @Override
    public ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId) {
        // get user layout
        return listLayoutRepository.getUserListLayout(userId, listLayoutId);
    }

    @Override
    public ListLayoutEntity getDefaultUserLayout(Long userId) {
        return listLayoutRepository.getDefaultUserLayout(userId);
    }

    @Override
    public ListLayoutEntity getStandardLayout() {
        return listLayoutRepository.getStandardLayout();
    }

}
