package com.meg.listshop.auth.data.entity;


import com.meg.listshop.auth.api.model.ClientType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_devices")
@GenericGenerator(
        name = "user_device_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "user_device_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "500"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class UserDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_device_sequence")
    @Column(name = "user_device_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String model;

    private String os;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "client_type")
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Column(name = "client_version")
    private String clientVersion;

    @Column(name = "build_number")
    private String buildNumber;

    @Column(name = "client_device_id")
    private String clientDeviceId;

    private String token;

    @Column(name = "last_login")
    private Date lastLogin;

    public UserDeviceEntity() {
        // jpa empty constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String version) {
        this.clientVersion = version;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getClientDeviceId() {
        return clientDeviceId;
    }

    public void setClientDeviceId(String clientDeviceId) {
        this.clientDeviceId = clientDeviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "UserDeviceEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", os='" + os + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", clientType=" + clientType +
                ", clientVersion='" + clientVersion + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", clientDeviceId='" + clientDeviceId + '\'' +
                ", token='" + token + '\'' +
                ", lastLogin=" + lastLogin +
                '}';
    }
}