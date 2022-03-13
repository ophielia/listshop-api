package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientVersions {
    @JsonProperty("ios_min_version")
    private String iosMinVersion;
    @JsonProperty("android_min_version")
    private String androidMinVersion;

    public ClientVersions() {
        // empty constructor for Jackson
    }

    public String getIosMinVersion() {
        return iosMinVersion;
    }

    public void setIosMinVersion(String iosMinVersion) {
        this.iosMinVersion = iosMinVersion;
    }

    public String getAndroidMinVersion() {
        return androidMinVersion;
    }

    public void setAndroidMinVersion(String androidMinVersion) {
        this.androidMinVersion = androidMinVersion;
    }
}
