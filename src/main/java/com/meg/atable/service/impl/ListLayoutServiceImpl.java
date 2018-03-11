package com.meg.atable.service.impl;

import com.meg.atable.api.model.Category;
import com.meg.atable.api.model.ListLayoutCategory;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.CategoryRelationEntity;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListLayoutServiceImpl implements ListLayoutService {

    @Autowired
    private ListLayoutCategoryRepository listLayoutCategoryRepository;

    @Autowired
    private ListLayoutRepository listLayoutRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<ListLayoutEntity> getListLayouts() {
        return listLayoutRepository.findAll();
    }

    @Override
    public ListLayoutEntity getListLayoutByType(ListLayoutType listLayoutType) {
        List<ListLayoutEntity> listLayoutEntities = listLayoutRepository.findByLayoutType(listLayoutType);
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

    public Map<Long, Long> getSubCategoryMappings(Long listLayoutId) {
        List<CategoryRelationEntity> relations = listLayoutRepository.getSubCategoryMappings(listLayoutId);
        Map<Long, Long> map = new HashMap<>();
        relations.forEach(c -> map.put(c.getChild().getId(), c.getParent().getId()));
        return map;
    }

    @Override
    public List<ListLayoutCategoryEntity> getListCategoriesForIds(Set<Long> categoryIds) {
        return listLayoutCategoryRepository.findAll(categoryIds);
    }

    @Override
    public List<Category> getStructuredCategories(ListLayoutEntity listLayout) {
        if (listLayout.getCategories() == null || listLayout.getCategories().isEmpty()) {
            return new ArrayList<Category>();
        }

        // gather categories
        Map<Long, Category> allCategories = new HashMap<>();
        listLayout.getCategories().forEach(c -> {
            // copy into listlayoutcategory
            ListLayoutCategory lc = (ListLayoutCategory) new ListLayoutCategory(c.getId())
                    .name(c.getName());
            lc = (ListLayoutCategory) lc.layoutId(c.getLayoutId());
            lc = (ListLayoutCategory) lc.tagEntities(c.getTags());
            allCategories.put(c.getId(), lc);
        });

        // structure subcategories
        structureCategories(allCategories, listLayout.getId());
        return allCategories.values().stream().collect(Collectors.toList());
    }

    @Override
    public void structureCategories(Map<Long, Category> filledCategories, Long listLayoutId) {
        Map<Long, Long> subCategoryMappings = getSubCategoryMappings(listLayoutId);
        for (Map.Entry<Long, Long> entry : subCategoryMappings.entrySet()) {
            Category child = filledCategories.get(entry.getKey());
            Category parent = filledCategories.get(entry.getValue());
            parent.addSubCategory(child);
        }
        for (Long childId : subCategoryMappings.keySet()) {
            filledCategories.remove(childId);
        }

        // sort subcategories in categories
        for (Category sortCategory : filledCategories.values()) {
            if (sortCategory.getSubCategories().isEmpty()) {
                continue;
            }
            sortCategory.getSubCategories()
                    .sort(Comparator.comparing(Category::getDisplayOrder));
        }

    }


}
