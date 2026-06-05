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

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
@Transactional
@Qualifier("LegacyLayoutService")
public class LegacyLayoutServiceImpl extends BaseLayoutServiceImpl implements LayoutService {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyLayoutServiceImpl.class);

    @Value("${service.layoutservice.default.layout.name:Default}")
    String defaultLayoutDefaultName;

    @Autowired
    public LegacyLayoutServiceImpl(ListLayoutRepository listLayoutRepository, ListLayoutCategoryRepository categoryRepository, TagRepository tagRepository, UserService userService) {
        super(listLayoutRepository, categoryRepository, tagRepository, userService);
    }

    @Override
    public List<ListLayoutEntity> getUserLayouts(UserEntity user) {
        return listLayoutRepository.getUserLayouts(user.getId());
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
}
