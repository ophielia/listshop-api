/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.LayoutCategoryDTO;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.LayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BaseLayoutServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(BaseLayoutServiceImpl.class);

    protected final ListLayoutRepository listLayoutRepository;
    protected final ListLayoutCategoryRepository categoryRepository;
    protected final TagRepository tagRepository;
    protected final UserService userService;

    @Value("${service.layoutservice.default.layout.name:Default}")
    String defaultLayoutDefaultName;

    @Autowired
    public BaseLayoutServiceImpl(ListLayoutRepository listLayoutRepository, ListLayoutCategoryRepository categoryRepository, TagRepository tagRepository, UserService userService) {
        this.listLayoutRepository = listLayoutRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    public ListLayoutEntity getUserListLayout(Long userId, Long listLayoutId) {
        // get user layout
        return listLayoutRepository.getUserListLayout(userId, listLayoutId);
    }

    public ListLayoutEntity getDefaultUserLayout(Long userId) {
        return listLayoutRepository.getDefaultUserLayout(userId);
    }

    public ListLayoutEntity getStandardLayout() {
        return listLayoutRepository.getStandardLayout();
    }

    public void addDefaultUserMappings(Long userId, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException {
        // get default user mappings - and send to next
        ListLayoutEntity defaultUserLayout = getOrCreateDefaultUserLayout(userId);
        addMappingsToLayout(defaultUserLayout, categoryId, tagIds);
    }

    public List<ListLayoutEntity> getUserLayouts(UserEntity user) {
        // NO-IMPL
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<ListLayoutEntity> getAllLayouts(Long userId) {
        // NO-IMPL
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<ListLayoutEntity> getAllLayoutsWithTag(Long userId, Long tagId) {

        List<ListLayoutEntity> layouts = new ArrayList<>();
        layouts.add(getFilledStandardLayout(userId, tagId));
        if (userId != null) {
            layouts.addAll(listLayoutRepository.getUserLayoutsWithTag(userId, tagId));
        }

        return layouts;
    }

    public void assignDefaultCategoryToTag(List<TagEntity> siblings, TagEntity tagToAssign) {
        Long idToAssign = null;
        if (!siblings.isEmpty()) {
            Set<Long> siblingsTagIds = siblings.stream().map(TagEntity::getId).collect(Collectors.toSet());
            idToAssign = listLayoutRepository.getDefaultCategoryForSiblings(siblingsTagIds);
        }

        if (idToAssign == null) {
            idToAssign = categoryRepository.getDefaultCategoryId();
        }

        Optional<ListLayoutCategoryEntity> toAssign = categoryRepository.findById(idToAssign);

        if (toAssign.isPresent()) {
            toAssign.get().addTag(tagToAssign);
        }

    }

    public void assignUserDefaultCategoriesToTag(List<TagEntity> siblings, TagEntity tagToAssign) {
        Long userId = tagToAssign.getUserId();
        if (userId == null) {
            // the only way to get the user_id is through the tag
            return;
        }
        Set<Long> idsToAssign = new HashSet<>();
        if (!siblings.isEmpty()) {
            Set<Long> siblingsTagIds = siblings.stream().map(TagEntity::getId).collect(Collectors.toSet());
            idsToAssign = listLayoutRepository.getUserCategoriesForSiblings(userId, siblingsTagIds);
        }

        if (idsToAssign.isEmpty()) {
            return;
        }

        List<ListLayoutCategoryEntity> toAssign = categoryRepository.getByIds(idsToAssign);

        toAssign.forEach(c -> c.addTag(tagToAssign));
    }

    public List<ListLayoutCategoryEntity> getUserCategories(String userName) {
        // NO-IMPL
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<ListLayoutCategoryEntity> getUserCategoriesForList(Long userLayoutId, Long listId) {
        return listLayoutRepository.findUserListCategoriesForList(userLayoutId, listId);

    }

    public List<ListLayoutCategoryEntity> getStandardCategoriesForList(Long listId) {
        return listLayoutRepository.findStandardCategoriesForList(listId);
    }

    public List<LayoutCategoryDTO> getDefaultCategories() {
        //NO-IMPL
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addTagToCategory(Long layoutCategoryId, TagEntity tag) {
        Optional<ListLayoutCategoryEntity> listLayoutEntityOpt = categoryRepository.findById(layoutCategoryId);
        if (!listLayoutEntityOpt.isPresent()) {
            return;
        }
        ListLayoutCategoryEntity categoryEntity = listLayoutEntityOpt.get();
        Set<TagEntity> tags = categoryEntity.getTags();
        if (tags.stream().anyMatch(t -> t.getId().equals(tag.getId()))) {
            return;
        }
        doAddCategory(categoryEntity, tag);

    }

    protected void removeTagFromCategory(ListLayoutCategoryEntity categoryEntity, TagEntity tag) {
        Set<TagEntity> tags = categoryEntity.getTags();
        if (!tags.stream().anyMatch(t -> t.getId().equals(tag.getId()))) {
            return;
        }
        tags.remove(tag);
        tag.getCategories().remove(categoryEntity);
        categoryEntity.setTags(tags);
        categoryRepository.save(categoryEntity);
    }

    protected void addMappingsToLayout(ListLayoutEntity listLayout, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException {
        if (tagIds.isEmpty()) {
            LOG.warn("Empty tag list sent to addMappingsToLayout [{}][{}]", listLayout.getId(), categoryId);
            return;
        }
        // retrieve category for mappings
        ListLayoutCategoryEntity givenCategory = categoryRepository.findById(categoryId).orElse(null);
        if (givenCategory == null) {
            LOG.warn("Template category in addMappingsToLayout not found [{}][{}]", listLayout.getId(), categoryId);
            throw new ObjectNotFoundException(String.format("Template category in addMappingsToLayout not found [%s][%s]", listLayout.getId(), categoryId));
        }
        Set<Long> mappingTagIds = new HashSet<>(tagIds);
        // delete any existing mappings in layout for tags
        deleteTagMappingsInLayout(listLayout.getId(), mappingTagIds);

        ListLayoutCategoryEntity mappingCategory = createNewCategoryIfNecessary(givenCategory, listLayout);
        // map the categories
        List<TagEntity> tagsToAssign = tagRepository.getTagsForIdList(mappingTagIds);
        tagsToAssign.forEach(tagEntity -> tagEntity.addCategory(mappingCategory));

    }

    protected ListLayoutCategoryEntity createNewCategoryIfNecessary(ListLayoutCategoryEntity categoryTemplate, ListLayoutEntity layout) {
        if (Objects.equals(categoryTemplate.getLayoutId(), layout.getId())) {
            return categoryTemplate;
        }
        // category belongs to a different layout - see if one with the same name exists in this layout
        ListLayoutCategoryEntity category = categoryRepository.findByNameInLayout(categoryTemplate.getName().trim(), layout.getId());
        if (category != null) {
            return category;
        }

        // category belongs to a different layout - make a new one in the passed layout
        category = new ListLayoutCategoryEntity();
        category.setLayoutId(layout.getId());
        category.setName(categoryTemplate.getName());
        category.setDisplayOrder(categoryTemplate.getDisplayOrder());
        ListLayoutCategoryEntity savedCategory = categoryRepository.save(category);
        layout.addCategory(savedCategory);
        return savedCategory;
    }

    private ListLayoutEntity getOrCreateDefaultUserLayout(Long userId) {
        ListLayoutEntity defaultLayout = listLayoutRepository.getDefaultUserLayout(userId);
        if (defaultLayout != null) {
            return defaultLayout;
        }
        ListLayoutEntity newDefault = new ListLayoutEntity();
        newDefault.setDefault(true);
        newDefault.setName(defaultLayoutDefaultName);
        newDefault.setUserId(userId);
        return listLayoutRepository.save(newDefault);
    }

    private void deleteTagMappingsInLayout(Long layoutId, Set<Long> tagIds) {
        List<TagEntity> tagsToDelete = listLayoutRepository.getTagsToDeleteFromLayout(layoutId, tagIds);

        tagsToDelete.forEach(t -> {
            // get category
            Optional<ListLayoutCategoryEntity> categoryToDelete = t.getCategories().stream()
                    .filter(c -> Objects.equals(c.getLayoutId(), layoutId))
                    .findFirst();
            if (categoryToDelete.isPresent()) {
                Optional<ListLayoutCategoryEntity> category = categoryRepository.findById(categoryToDelete.get().getId());
                if (category.isPresent()) {

                    // do deletion
                    category.get().removeTag(t);
                }
            }

        });

    }

    private ListLayoutEntity getFilledStandardLayout(Long userId, Long tagId) {
        ListLayoutEntity standardLayout = getStandardLayout();

        return listLayoutRepository.fillLayout(userId, tagId,standardLayout);
    }

    private void doAddCategory(ListLayoutCategoryEntity categoryEntity, TagEntity tag) {

        categoryEntity.getTags().add(tag);
        tag.getCategories().add(categoryEntity);

        categoryRepository.save(categoryEntity);
    }

}
