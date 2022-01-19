package com.meg.listshop.lmt.api.model;


public class MergeResultResource extends AbstractListShopResource implements ListShopModel {

    private final MergeResult mergeResult;

    public MergeResultResource(MergeResult dish) {
        this.mergeResult = dish;
    }

    public MergeResult getMergeResult() {
        return mergeResult;
    }

    @Override
    public String getRootPath() {
        return "mergeresult";
    }

    @Override
    public String getResourceId() {
        return null;
    }
}