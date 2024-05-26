package com.meg.listshop.auth.service;

public enum UserPropertyKey {
    TestEmailSent("test_info_sent"),
    TestInfoRequested("request_test_info"),
    PreferredDomain("preferred_domain");

    private final String display;

    UserPropertyKey(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }
}

