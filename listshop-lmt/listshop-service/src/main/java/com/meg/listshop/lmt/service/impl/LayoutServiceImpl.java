package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.listshop.lmt.data.entity.ListLayoutEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ListLayoutCategoryRepository;
import com.meg.listshop.lmt.data.repository.ListLayoutRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.LayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
@Transactional
public class LayoutServiceImpl implements LayoutService {

    private static final Logger LOG = LoggerFactory.getLogger(LayoutServiceImpl.class);

    private final ListLayoutRepository listLayoutRepository;
    private final ListLayoutCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    @Value("${service.layoutservice.default.layout.name:Default}")
    String defaultLayoutDefaultName;

    @Autowired
    public LayoutServiceImpl(ListLayoutRepository listLayoutRepository, ListLayoutCategoryRepository categoryRepository, TagRepository tagRepository, UserService userService) {
        this.listLayoutRepository = listLayoutRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
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


    @Override
    public void addDefaultUserMappings(Long userId, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException {
        // get default user mappings - and send to next
        ListLayoutEntity defaultUserLayout = getOrCreateDefaultUserLayout(userId);
        addMappingsToLayout(defaultUserLayout, categoryId, tagIds);
    }

    @Override
    public List<ListLayoutEntity> getUserLayouts(UserEntity user) {
        return listLayoutRepository.getUserLayouts(user.getId());
    }

    @Override
    public ListLayoutEntity getFilledStandardLayout(Long userId) {
        ListLayoutEntity standardLayout = getStandardLayout();

        return listLayoutRepository.fillLayout(userId, standardLayout);
    }

    @Override
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

    @Override
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

    @Override
    public List<ListLayoutCategoryEntity> getUserCategories(String userName) {
        UserEntity user = userService.getUserByUserEmail(userName);
        if (user == null) {
            LOG.error("No user found for username [{}]", userName);
            return new ArrayList<>();
        }
        // get default layout for user
        ListLayoutEntity userDefaultLayout = getDefaultUserLayout(user.getId());

        // return categories for this layout
        return getAvailableCategoriesForLayout(userDefaultLayout);
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

    private void addMappingsToLayout(ListLayoutEntity listLayout, Long categoryId, List<Long> tagIds) throws ObjectNotFoundException {
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
        String beep = "bop";
    }

    private ListLayoutCategoryEntity createNewCategoryIfNecessary(ListLayoutCategoryEntity categoryTemplate, ListLayoutEntity layout) {
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

    @Override
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
        tags.add(tag);
        tag.getCategories().add(categoryEntity);
        categoryEntity.setTags(tags);
        categoryRepository.save(categoryEntity);
    }
}
