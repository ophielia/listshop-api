package com.meg.atable.service.impl;

import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ListLayoutCategoryRepository;
import com.meg.atable.data.repository.ListLayoutRepository;
import com.meg.atable.data.repository.SlotRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishService;
import com.meg.atable.service.ListLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListLayoutServiceImpl implements ListLayoutService {

    @Autowired
    private UserService userService;

    @Autowired
    private ListLayoutCategoryRepository listLayoutCategoryRepository;

    @Autowired
    private ListLayoutRepository listLayoutRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DishService dishService;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<ListLayoutEntity> getListLayouts() {
        return listLayoutRepository.findAll();
    }

    @Override
    public ListLayoutEntity getListLayoutByType(ListLayoutType listLayoutType) {
        List<ListLayoutEntity> listLayoutEntities  = listLayoutRepository.findByLayoutType(listLayoutType);
        if (listLayoutEntities == null || listLayoutEntities.isEmpty()) {
            return null;
        }
        return listLayoutEntities.get(0);
    }

    @Override
    public ListLayoutEntity createListLayout(ListLayoutEntity listLayoutEntity) {
        // createListLayout with repository and return
        listLayoutEntity = listLayoutRepository.save(listLayoutEntity);
        return listLayoutEntity;
    }

    @Override
    public ListLayoutEntity getListLayoutById(Long listLayoutId) {
        return listLayoutRepository.findOne(listLayoutId);
    }

    @Override
    public void deleteListLayout(Long listLayoutId) {
        listLayoutRepository.delete(listLayoutId);
    }

    @Override
    public void addCategoryToListLayout(Long listLayoutId, ListLayoutCategoryEntity entity) {
        // get list
        ListLayoutEntity layoutEntity = listLayoutRepository.findOne(listLayoutId);

        // save listlayout category
        entity.setLayoutId(layoutEntity.getId());
        ListLayoutCategoryEntity result = listLayoutCategoryRepository.save(entity);
        // add to list layout category list
        List<ListLayoutCategoryEntity> categories = layoutEntity.getCategories();
        if (categories == null) {
            categories = new ArrayList<>();
        }
        categories.add(result);
        layoutEntity.setCategories(categories);

        // save list layout category
        listLayoutRepository.save(layoutEntity);
    }

    @Override
    public void deleteCategoryFromListLayout(Long listLayoutId, Long layoutCategoryId) {
// get list
        ListLayoutEntity layoutEntity = listLayoutRepository.findOne(listLayoutId);
        // filter category to delete from list categories
        List<ListLayoutCategoryEntity> filtered = layoutEntity.getCategories().stream()
                .filter(c -> c.getId().longValue() != layoutCategoryId.longValue())
                .collect(Collectors.toList());
        // delete category
        listLayoutCategoryRepository.delete(layoutCategoryId);

        // set categories in list layout and save
        layoutEntity.setCategories(filtered);
        listLayoutRepository.save(layoutEntity);
    }

    @Override
    public ListLayoutCategoryEntity updateListLayoutCategory(Long listLayoutId, ListLayoutCategoryEntity listLayoutCategory) {
        if (listLayoutCategory == null || listLayoutCategory.getId() == null) {
            return null;
        }
        // get layout category
        ListLayoutCategoryEntity layoutCategoryEntity = listLayoutCategoryRepository.findOne(listLayoutCategory.getId());
        if (layoutCategoryEntity == null) {
            return null;
        }
        // ensure that the list layout id is the same
        if (layoutCategoryEntity.getLayoutId().longValue() != listLayoutId.longValue()) {
            return null;
        }
        // update layout category name
        layoutCategoryEntity.setName(listLayoutCategory.getName());
        // save layout category and return

        return listLayoutCategoryRepository.save(layoutCategoryEntity);
    }

    @Override
    public List<TagEntity> getUncategorizedTagsForList(Long listLayoutId) {
        return tagRepository.getUncategorizedTagsForList(listLayoutId);
    }

    @Override
    public List<TagEntity> getTagsForLayoutCategory(Long layoutCategoryId) {
        return tagRepository.getTagsForLayoutCategory(layoutCategoryId);
    }

    @Override
    public void addTagsToCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList) {
        // get  category
        ListLayoutCategoryEntity categoryEntity = listLayoutCategoryRepository.findOne(layoutCategoryId);
        // assure list owns category
        if (categoryEntity.getLayoutId().longValue() != listLayoutId.longValue()) {
            return;
        }

        // get tags for list
        List<TagEntity> tagCategories = tagRepository.getTagsForLayoutCategory(layoutCategoryId);
        List<Long> tagIdsToAdd = new ArrayList<>();

        // create filtered list - checking for tags already in list
        List<Long> alreadyInList = tagCategories.stream()
                .filter(t -> tagIdList.contains(t.getId()))
                .map(TagEntity::getId)
                .collect(Collectors.toList());
        // if any tags already in list, filter from add list
        if (!alreadyInList.isEmpty()) {
            List<Long> withoutDoubles = tagIdList.stream()
                    .filter(i -> !alreadyInList.contains(i))
                    .collect(Collectors.toList());
            tagIdsToAdd = withoutDoubles;
        } else {
            tagIdsToAdd = tagIdList;
        }

        // set tags in category
        List<TagEntity> tagsToAdd = tagRepository.findAll(tagIdsToAdd);
        tagCategories.addAll(tagsToAdd);
        categoryEntity.setTags(tagCategories);

        // save category
        listLayoutCategoryRepository.save(categoryEntity);
    }


    @Override
    public void deleteTagsFromCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList) {
        // get  category
        ListLayoutCategoryEntity categoryEntity = listLayoutCategoryRepository.findOne(layoutCategoryId);
        // assure list owns category
        if (categoryEntity.getLayoutId().longValue() != listLayoutId.longValue()) {
            return;
        }

        // get tags for list
        List<TagEntity> tagCategories = tagRepository.getTagsForLayoutCategory(layoutCategoryId);

        // create filtered list - getting all tags not in list
        List<Long> filteredList = tagCategories.stream()
                .filter(t -> !tagIdList.contains(t.getId()))
                .map(TagEntity::getId)
                .collect(Collectors.toList());

        // set tags in category
        List<TagEntity> filteredTagList = tagRepository.findAll(filteredList);
        categoryEntity.setTags(filteredTagList);

        // save category
        listLayoutCategoryRepository.save(categoryEntity);
    }


}
