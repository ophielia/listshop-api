package com.meg.atable.data.entity;

import com.meg.atable.api.model.ListLayoutType;

import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
public class ListLayoutEntity {

    private Long layoutId;

    private ListLayoutType layoutType;

    private List<ListCategoryEntity> categories;
}
