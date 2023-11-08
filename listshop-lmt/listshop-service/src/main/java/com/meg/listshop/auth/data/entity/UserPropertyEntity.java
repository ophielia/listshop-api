package com.meg.listshop.auth.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "USER_PROPERTIES")
@GenericGenerator(
        name = "user_properties_id_seq",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "authority_id_seq"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "500"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class UserPropertyEntity {

    @Id
    @Column(name = "user_property_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_properties_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "property_key")
    private String key;

    @Column(name = "property_value")
    private String value;

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
                '}';
    }
}