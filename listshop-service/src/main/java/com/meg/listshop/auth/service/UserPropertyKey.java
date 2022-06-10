package com.meg.listshop.auth.service;

public enum UserPropertyKey {
    TestInfoRequested("request_test_info");

    private final String display;

    UserPropertyKey(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}

