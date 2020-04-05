package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.lmt.api.model.Category;
import com.meg.listshop.lmt.api.model.ListLayoutCategory;
import com.meg.listshop.lmt.api.model.ListLayoutType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.repository.CategoryRelationRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.ListLayoutException;
import com.meg.listshop.lmt.service.ListLayoutProperties;
import com.meg.listshop.lmt.service.ListLayoutService;
import com.meg.listshop.lmt.service.ShoppingListProperties;
import com.meg.listshop.lmt.service.tag.TagService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListLayoutServiceImpl implements ListLayoutService {


    private ListLayoutCategoryRepository listLayoutCategoryRepository;
    private ListLayoutProperties listLayoutProperties;
    private CategoryRelationRepository categoryRelationRepository;
    private ListLayoutRepository listLayoutRepository;
    private TagRepository tagRepository;
    private TagService tagService;
    private ShoppingListProperties shoppingListProperties;

    @Autowired
    public ListLayoutServiceImpl(ListLayoutCategoryRepository listLayoutCategoryRepository,
                                 ListLayoutProperties listLayoutProperties,
                                 CategoryRelationRepository categoryRelationRepository,
                                 ListLayoutRepository listLayoutRepository,
                                 TagRepository tagRepository,
                                 TagService tagService,
                                 ShoppingListProperties shoppingListProperties) {
        this.listLayoutCategoryRepository = listLayoutCategoryRepository;
        this.listLayoutProperties = listLayoutProperties;
        this.categoryRelationRepository = categoryRelationRepository;
        this.listLayoutRepository = listLayoutRepository;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
        this.shoppingListProperties = shoppingListProperties;
    }

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
        return listLayoutRepository.save(listLayoutEntity);
    }

    @Override
    public ListLayoutEntity getDefaultListLayout() {
        ListLayoutType layoutType = shoppingListProperties.getDefaultListLayoutType();

        List<ListLayoutEntity> listLayoutEntity =  listLayoutRepository.findByLayoutType(layoutType);

        if (listLayoutEntity == null || listLayoutEntity.isEmpty()) {
            return null;
        }
        return listLayoutEntity.get(0);
    }

    @Override
    public ListLayoutEntity getListLayoutById(Long listLayoutId) {

        Optional<ListLayoutEntity> listLayoutEntityOpt =  listLayoutRepository.findById(listLayoutId);
        return listLayoutEntityOpt.orElse(null);
    }

    @Override
    public void deleteListLayout(Long listLayoutId) {
        listLayoutRepository.deleteById(listLayoutId);
    }

    @Override
    public void addCategoryToListLayout(Long listLayoutId, ListLayoutCategoryEntity entity) {
        // get list
        Optional<ListLayoutEntity> listLayoutEntityOpt =  listLayoutRepository.findById(listLayoutId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
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

        // save relationship info for new layout entity
        try {
            addCategoryToParent(result.getId(), 0L);
        } catch (ListLayoutException e) {
            // TODO - log exception here
        }
    }

    @Override
    public void deleteCategoryFromListLayout(Long listLayoutId, Long layoutCategoryId) throws ListLayoutException {
// get list
        Optional<ListLayoutEntity> listLayoutEntityOpt =  listLayoutRepository.findById(listLayoutId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutEntity layoutEntity = listLayoutEntityOpt.get();
        // filter category to delete from list categories
        List<ListLayoutCategoryEntity> filtered = layoutEntity.getCategories().stream()
                .filter(c -> c.getId().longValue() != layoutCategoryId.longValue())
                .collect(Collectors.toList());
        // move any subcategories
        List<ListLayoutCategoryEntity> subcategories = listLayoutCategoryRepository.getSubcategoriesForOrder(listLayoutId, layoutCategoryId);
        if (subcategories != null && !subcategories.isEmpty()) {
            for (ListLayoutCategoryEntity subcat : subcategories) {
                addCategoryToParent(subcat.getId(), 0L);
            }
        }
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
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt =  listLayoutCategoryRepository.findById(listLayoutId);
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
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt =  listLayoutCategoryRepository.findById(layoutCategoryId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutCategoryEntity categoryEntity = listLayoutEntityOpt.get();
        // assure list owns category
        if (!categoryEntity.getLayoutId().equals(listLayoutId) ) {
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
            List<Long> withoutDoubles = tagIdList.stream()
                    .filter(i -> !alreadyInList.contains(i))
                    .collect(Collectors.toList());
            tagIdsToAdd = withoutDoubles;
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

    public Map<Long, Long> getSubCategoryMappings(Long listLayoutId) {
        List<CategoryRelationEntity> relations = listLayoutRepository.getSubCategoryMappings(listLayoutId);
        Map<Long, Long> map = new HashMap<>();
        if (relations == null) {
            return map;
        }
        relations.forEach(c -> {if (c.getParent() != null) {map.put(c.getChild().getId(), c.getParent().getId());}});
        return map;
    }

    @Override
    public List<ListLayoutCategoryEntity> getListCategoriesForIds(Set<Long> categoryIds) {
        return listLayoutCategoryRepository.findAllById(categoryIds);
    }

    @Override
    public List<ListLayoutCategoryEntity> getListCategoriesForLayout(Long layoutId) {
        return listLayoutCategoryRepository.findByLayoutIdEquals(layoutId);
    }

    @Override
    public List<Category> getStructuredCategories(ListLayoutEntity listLayout) {
        if (listLayout.getCategories() == null || listLayout.getCategories().isEmpty()) {
            return new ArrayList<>();
        }

        // gather categories
        Map<Long, Category> allCategories = new HashMap<>();
        listLayout.getCategories().forEach(c -> {
            // copy into listlayoutcategory
            ListLayoutCategory lc = (ListLayoutCategory) new ListLayoutCategory(c.getId())
                    .name(c.getName());
            lc = (ListLayoutCategory) lc.layoutId(c.getLayoutId());
            lc = (ListLayoutCategory) lc.tagEntities(c.getTags());
            lc = (ListLayoutCategory) lc.displayOrder(c.getDisplayOrder());
            allCategories.put(c.getId(), lc);
        });

        // structure subcategories
        structureCategories(allCategories, listLayout.getId(), false);
        List<Category> mainCategorySort = allCategories.values().stream().sorted(Comparator.comparing(Category::getDisplayOrder)).collect(Collectors.toList());
        return mainCategorySort;
    }

    @Override
    public void structureCategories(Map<Long, Category> filledCategories, Long listLayoutId, boolean pruneSubcategories) {

        Map<Long, Long> subCategoryMappings = getSubCategoryMappings(listLayoutId);
        for (Map.Entry<Long, Long> entry : subCategoryMappings.entrySet()) {
            Category child = filledCategories.get(entry.getKey());
            Category parent = filledCategories.get(entry.getValue());
            if (child == null || (pruneSubcategories && child.isEmpty())) {
                continue;
            }
            if (parent != null ) {
                parent.addSubCategory(child);
            }
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

    @Override
    public void addCategoryToParent(Long categoryId, Long parentId) throws ListLayoutException {

        if (categoryId == null || parentId == null) {
            throw new ListLayoutException("Invalid Parameters in addCategoryToParent: categoryId[" + categoryId + "] parentId[" + parentId + "]");
        }
        // get category
        Optional<ListLayoutCategoryEntity> categoryOpt = listLayoutCategoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new ListLayoutException("Category not found in addCategoryToParent: categoryId[" + categoryId + "]");
        }
        ListLayoutCategoryEntity category = categoryOpt.get();
        // get parent category
        ListLayoutCategoryEntity parent = null;
        if (!parentId.equals(0L)) {
            Optional<ListLayoutCategoryEntity> parentOpt = listLayoutCategoryRepository.findById(parentId);
            if (!parentOpt.isPresent()) {
                throw new ListLayoutException("Parent not found in addCategoryToParent: parentId[" + parentId + "]");
            }
            parent = parentOpt.get();
        }

        // check that both are in the same layout
        if (parent != null && !parent.getLayoutId().equals(category.getLayoutId())) {
                throw new ListLayoutException("Parent and child don't belong to same layout in addCategoryToParent: childId[" + categoryId + "] parentId[" + parentId + "]");
        }

        // pull relationship
Long relationshipId = getCategoryRelationForCategory(category);
        // update relationship
            Optional<CategoryRelationEntity> relationshipOpt = categoryRelationRepository.findById(relationshipId);
            if (!relationshipOpt.isPresent()) {
                return;
            }
            CategoryRelationEntity relationship = relationshipOpt.get();
        relationship.setParent(parent);
        relationship.setChild(category);
        categoryRelationRepository.save(relationship);

        // get new display order
        Integer newOrder = getDisplayOrderInCategory(category.getLayoutId(), parent);
        category.setDisplayOrder(newOrder);
        listLayoutCategoryRepository.save(category);
    }

    private Long getCategoryRelationForCategory(ListLayoutCategoryEntity category) throws ListLayoutException {


        List<CategoryRelationEntity> relationships = categoryRelationRepository.findCategoryRelationsByChildId(category.getId());
        CategoryRelationEntity relationship;
        if (relationships == null || relationships.isEmpty()) {
            // create new relationship
            relationship = new CategoryRelationEntity();
            relationship.setChild(category);
            relationship = categoryRelationRepository.save(relationship);
            return relationship.getId();
        } else {
            if (relationships.size() > 1) {
                throw new ListLayoutException("More than one relationship found for category [" + category.getId() + "]");
            }
            return relationships.get(0).getId();
        }
    }

    @Override
    public void moveCategory(Long categoryId, boolean moveUp) throws ListLayoutException {
        if (categoryId == null) {
            throw new ListLayoutException("Null categoryId passed to moveCategory. Can't do much");
        }
        // retrieve category
        Optional<ListLayoutCategoryEntity> categoryOpt = listLayoutCategoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new ListLayoutException("No category found for categoryId [" + categoryId + "] in moveCategory. ");
        }
        ListLayoutCategoryEntity category = categoryOpt.get();

        //  get category with which to swap
        ListLayoutCategoryEntity swapCategory = getCategoryForSwap(category, moveUp);
        if (swapCategory == null) {
            throw new ListLayoutException("No swapCategory found for categoryId [" + categoryId + "] in moveCategory. ");
        }

        // swap categories
        Integer holding = swapCategory.getDisplayOrder();
        swapCategory.setDisplayOrder(category.getDisplayOrder());
        category.setDisplayOrder(holding);

        // save both categories
        listLayoutCategoryRepository.save(swapCategory);
        listLayoutCategoryRepository.save(category);

        // return
    }

    @Override
    public List<Pair<ItemEntity, ListLayoutCategoryEntity>> getItemChangesWithCategories(Long listLayoutId, List<ItemEntity> changedItems) {
        if (changedItems.isEmpty()) {
            // nothing to do here but return
            return new ArrayList<>();
        }
        Set<Long> itemIds = changedItems.stream()
                .map(ItemEntity::getId)
                .collect(Collectors.toSet());

        List<Object[]> rawRelations = listLayoutCategoryRepository.getItemToCategoryRelationshipsForItemIds(itemIds, listLayoutId);

        Map<Long, Long> itemToCategoryLookup = new HashMap<>();
        Map<Long, ListLayoutCategoryEntity> categoryLookup = new HashMap<>();
        for (Object[] result : rawRelations) {
            Long itemId = result[0] == null ? 0L : ((BigInteger) result[0]).longValue();
            Long categoryId = ((BigInteger) result[1]).longValue();
            itemToCategoryLookup.put(itemId, categoryId);
        }

        // get categories from lookup
        List<ListLayoutCategoryEntity> categoryEntities =
                listLayoutCategoryRepository.findAllById(itemToCategoryLookup.values().stream().collect(Collectors.toSet()));
        categoryEntities.stream().forEach(c -> categoryLookup.put(c.getId(), c));

        List<Pair<ItemEntity, ListLayoutCategoryEntity>> results = new ArrayList<>();
        for (ItemEntity itemEntity : changedItems) {
            if (!itemToCategoryLookup.containsKey(itemEntity.getId()) ||
                    !categoryLookup.containsKey(itemToCategoryLookup.get(itemEntity.getId()))) {
                continue;
            }
            ListLayoutCategoryEntity category = categoryLookup.get(itemToCategoryLookup.get(itemEntity.getId()));

            Pair<ItemEntity, ListLayoutCategoryEntity> pair = new Pair<>(itemEntity, category);
            results.add(pair);
        }
        return results;

    }

    @Override
    public void assignTagToDefaultCategories(TagEntity newtag) {
        // repull tag from db
        TagEntity tagToUpdate = tagService.getTagById(newtag.getId());
        // get default categories for all list layouts
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
    public List<Pair<TagEntity, ListLayoutCategoryEntity>> getTagCategoryChanges(Long listLayoutId, Date changedAfter) {


        List<TagEntity> changedTags = tagRepository.getTagsWithCategoriesChangedAfter(changedAfter, listLayoutId);
        Set<Long> tagIds = changedTags.stream()
                .map(TagEntity::getId)
                .collect(Collectors.toSet());

        // get category lookup for tag ids
        List<Object[]> rawRelations = listLayoutCategoryRepository.getTagToCategoryRelationshipsForTagIds(tagIds, listLayoutId);

        Map<Long, Long> tagToCategoryIdLookup = new HashMap<>();
        Map<Long, ListLayoutCategoryEntity> categoryLookup = new HashMap<>();
        for (Object[] result : rawRelations) {
            Long tagId = result[0] == null ? 0L : ((BigInteger) result[0]).longValue();
            Long categoryId = ((BigInteger) result[1]).longValue();
            tagToCategoryIdLookup.put(tagId, categoryId);
        }

        // get categories from lookup
        List<ListLayoutCategoryEntity> categoryEntities =
                listLayoutCategoryRepository.findAllById(tagToCategoryIdLookup.values().stream().collect(Collectors.toSet()));
        categoryEntities.stream().forEach(c -> categoryLookup.put(c.getId(), c));

        List<Pair<TagEntity, ListLayoutCategoryEntity>> results = new ArrayList<>();
        for (TagEntity tag : changedTags) {
            if (!tagToCategoryIdLookup.containsKey(tag.getId()) ||
                    !categoryLookup.containsKey(tagToCategoryIdLookup.get(tag.getId()))) {
                continue;
            }
            ListLayoutCategoryEntity category = categoryLookup.get(tagToCategoryIdLookup.get(tag.getId()));
            Pair<TagEntity, ListLayoutCategoryEntity> pair = new Pair<>(tag, category);
            results.add(pair);
        }
        return results;
    }

    private ListLayoutCategoryEntity getCategoryForSwap(ListLayoutCategoryEntity category, boolean moveUp) throws ListLayoutException {
        // get category relationship
        List<CategoryRelationEntity> relationships = categoryRelationRepository.findCategoryRelationsByChildId(category.getId());
        if (relationships == null || relationships.size() != 1) {
            throw new ListLayoutException("Bad results for finding relationship for category [" + category + "].");
        }
        // get list depending upon parent and direction
        CategoryRelationEntity relationship = relationships.get(0);
        boolean isBaseCategory = relationship.getParent() == null;
        List<ListLayoutCategoryEntity> listForSwapout;
        if (isBaseCategory && moveUp) {
            listForSwapout = listLayoutCategoryRepository.getCategoriesAbove(category.getLayoutId(), category.getDisplayOrder());
        } else if (isBaseCategory) {
            listForSwapout = listLayoutCategoryRepository.getCategoriesBelow(category.getLayoutId(), category.getDisplayOrder());

        } else if (!isBaseCategory && moveUp) {
            listForSwapout = listLayoutCategoryRepository.getSubcategoriesAbove(category.getLayoutId(), relationship.getParent().getId(), category.getDisplayOrder());

        } else if (!isBaseCategory) {
            listForSwapout = listLayoutCategoryRepository.getSubcategoriesBelow(category.getLayoutId(), relationship.getParent().getId(), category.getDisplayOrder());

        } else {
            throw new ListLayoutException("Shoudn't arrive here - error case.");
        }

        if (listForSwapout == null || listForSwapout.isEmpty()) {
            // no eligible categories found
            return null;
        }

        return listForSwapout.get(0);

    }

    private Integer getDisplayOrderInCategory(Long listLayoutId, ListLayoutCategoryEntity parent) {
        Integer lastCategory = null;
        List<ListLayoutCategoryEntity> layouts;
        if (parent == null) {
            layouts = listLayoutCategoryRepository.getCategoriesForOrder(listLayoutId);

        } else {
            layouts = listLayoutCategoryRepository.getSubcategoriesForOrder(listLayoutId, parent.getId());

        }
        if (layouts != null && !layouts.isEmpty()) {
            lastCategory = layouts.get(0).getDisplayOrder();
        }

        // add increment
        Integer increment = listLayoutProperties.getDispOrderIncrement();
        return lastCategory != null ? lastCategory + increment : increment;

    }

    private List<ListLayoutCategoryEntity> getAllDefaultCategories() {
        return listLayoutCategoryRepository.findByIsDefaultTrue();
    }
}
