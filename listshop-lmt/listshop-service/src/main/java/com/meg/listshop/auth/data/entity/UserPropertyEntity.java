package com.meg.listshop.auth.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "USER_PROPERTIES")
public class UserPropertyEntity {

    @Id
    @Column(name = "user_property_id")
    @Tsid
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "property_key")
    private String key;

    @Column(name = "property_value")
    private String value;

    @Column(name = "is_system")
    private Boolean isSystem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSystem() {
        return isSystem;
    }

    public void setSystem(Boolean system) {
        isSystem = system;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPropertyEntity that = (UserPropertyEntity) o;
        return id.equals(that.id) && user.equals(that.user) && key.equals(that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, key, value);
    }

    @Override
    public String toString() {
        return "UserPropertyEntity{" +
                "id=" + id +
                ", user=" + user +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", system='" + isSystem + '\'' +
                '}';
    }
}