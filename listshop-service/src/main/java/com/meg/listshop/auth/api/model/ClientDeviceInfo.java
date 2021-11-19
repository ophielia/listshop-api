package com.meg.listshop.auth.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by margaretmartin on 12/04/2018.
 */
public class ClientDeviceInfo {

    private String name;
    private String model;
    private String os;
    private String osVersion;
    private ClientType clientType;
    private String clientVersion;
    private String buildNumber;
    private String deviceId;

    public ClientDeviceInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @JsonProperty("os_version")
    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @JsonProperty("client_type")
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    @JsonProperty("client_version")
    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    @JsonProperty("build_number")
    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "ClientDeviceInfo{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", os='" + os + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", clientType=" + clientType +
                ", version='" + clientVersion + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
