/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.api.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ListLayoutList {

    @JsonProperty("list_layouts")
    private List<ListLayout> listLayouts;

    public ListLayoutList() {
    }

    public ListLayoutList(List<ListLayout> listLayouts) {
        this.listLayouts = listLayouts;
    }

    public List<ListLayout> getListLayouts() {
        return listLayouts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ListLayoutList dishList = (ListLayoutList) o;
        return Objects.equals(listLayouts, dishList.listLayouts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(listLayouts);
    }

    @Override
    public String toString() {
        return "ListLayoutList{" +
                "listLayouts=" + listLayouts +
                '}';
    }

}
