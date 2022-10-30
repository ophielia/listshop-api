package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.ListLayoutType;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutException;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.ListSearchService;
import com.meg.listshop.lmt.service.categories.ListLayoutCategoryPojo;
import com.meg.listshop.lmt.service.categories.ListShopCategory;
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
    public List<ListLayoutEntity> getListLayouts() {
        return listLayoutRepository.findAll();
    }

    @Override
    public ListLayoutEntity createListLayout(ListLayoutEntity listLayoutEntity) {
        // createListLayout with repository and return
        return listLayoutRepository.save(listLayoutEntity);
    }

    @Override
    public ListLayoutEntity getDefaultListLayout() {
        //MM layout
        //ListLayoutType layoutType = shoppingListProperties.getDefaultListLayoutType();
        ListLayoutType layoutType = ListLayoutType.All;

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
        ListLayoutType layoutType = ListLayoutType.All;
        //ListLayoutType layoutType = shoppingListProperties.getDefaultListLayoutType();

        return null;//getDefaultCategoryForLayoutType(layoutType);
    }

    @Override
    public ListLayoutEntity getListLayoutById(Long listLayoutId) {

        Optional<ListLayoutEntity> listLayoutEntityOpt = listLayoutRepository.findById(listLayoutId);
        return listLayoutEntityOpt.orElse(null);
    }

    @Override
    public void deleteListLayout(Long listLayoutId) {
        listLayoutRepository.deleteById(listLayoutId);
    }

    @Override
    public Long addCategoryToListLayout(Long listLayoutId, ListLayoutCategoryEntity entity) {
        // get list
        Optional<ListLayoutEntity> listLayoutEntityOpt = listLayoutRepository.findById(listLayoutId);
        if (!listLayoutEntityOpt.isPresent()) {
            throw new ObjectNotFoundException("list layout not found for id :" + listLayoutId);
        }
        ListLayoutEntity layoutEntity = listLayoutEntityOpt.get();

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

        return result.getId();
    }

    @Override
    public void deleteCategoryFromListLayout(Long listLayoutId, Long layoutCategoryId) throws ListLayoutException {
// get list
        Optional<ListLayoutEntity> listLayoutEntityOpt = listLayoutRepository.findById(listLayoutId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutEntity layoutEntity = listLayoutEntityOpt.get();
        // filter category to delete from list categories
        List<ListLayoutCategoryEntity> filtered = layoutEntity.getCategories().stream()
                .filter(c -> c.getId().longValue() != layoutCategoryId.longValue())
                .collect(Collectors.toList());

        listLayoutCategoryRepository.flush();

        // delete category
        listLayoutCategoryRepository.deleteById(layoutCategoryId);


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
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt = listLayoutCategoryRepository.findById(listLayoutId);
        if (!listLayoutEntityOpt.isPresent()) {
            return null;
        }
        ListLayoutCategoryEntity layoutCategoryEntity = listLayoutEntityOpt.get();
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
        if (tags.stream().filter(t -> t.getId() == tag.getId()).findFirst().isPresent()) {
            return;
        }
        tags.add(tag);
        tag.getCategories().add(categoryEntity);
        categoryEntity.setTags(tags);
        listLayoutCategoryRepository.save(categoryEntity);
    }


    @Override
    public void deleteTagsFromCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList) {
        // get  category
        Optional<ListLayoutCategoryEntity> categoryEntityOpt = listLayoutCategoryRepository.findById(layoutCategoryId);
        if (!categoryEntityOpt.isPresent()) {
            return;
        }
        ListLayoutCategoryEntity categoryEntity = categoryEntityOpt.get();
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
        List<TagEntity> filteredTagList = tagRepository.findAllById(filteredList);
        for (TagEntity tagToUpdate : filteredTagList) {
            tagToUpdate.setCategoryUpdatedOn(new Date());
            //MM TODO update this
        }

        categoryEntity.setTags(filteredTagList);

        // save category
        listLayoutCategoryRepository.save(categoryEntity);
    }

    @Override
    public List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId) {
        return listLayoutCategoryRepository.findByLayoutIdEquals(layoutId);
    }

    @Override
    public List<ListLayoutCategoryPojo> getStructuredCategories(ListLayoutEntity listLayout) {
        if (listLayout.getCategories() == null || listLayout.getCategories().isEmpty()) {
            return new ArrayList<>();
        }

        // gather categories
        Map<Long, ListShopCategory> allCategories = new HashMap<>();
        listLayout.getCategories().forEach(c -> {
            // copy into listlayoutcategory
            ListLayoutCategoryPojo lc = (ListLayoutCategoryPojo) new ListLayoutCategoryPojo(c.getId())
                    .name(c.getName());
            lc = (ListLayoutCategoryPojo) lc.layoutId(c.getLayoutId());
            lc = (ListLayoutCategoryPojo) lc.tagEntities(c.getTags());
            lc = (ListLayoutCategoryPojo) lc.displayOrder(c.getDisplayOrder());
            allCategories.put(c.getId(), lc);
        });

        List<ListLayoutCategoryPojo> mainCategorySort = allCategories.values()
                .stream()
                .map(r -> (ListLayoutCategoryPojo) r)
                .sorted(Comparator.comparing(ListShopCategory::getDisplayOrder))
                .collect(Collectors.toList());
        return mainCategorySort;
    }

    @Override
    public void assignTagToDefaultCategories(TagEntity newtag) {
        // repull tag from db
        TagEntity tagToUpdate = tagService.getTagById(newtag.getId());
        // get default categories for all list categories
        List<ListLayoutCategoryEntity> defaultCategories = getAllDefaultCategories();

        // for each category, assign the category to the tag, and the tag to the category
        for (ListLayoutCategoryEntity category : defaultCategories) {
            tagToUpdate.getCategories().add(category);
            category.getTags().add(tagToUpdate);
        }

        tagService.save(tagToUpdate);
        listLayoutCategoryRepository.saveAll(defaultCategories);

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

    private List<ListLayoutCategoryEntity> getAllDefaultCategories() {
        return listLayoutCategoryRepository.findByIsDefaultTrue();
    }


}
