package com.meg.atable.auth.data.entity;


import com.meg.atable.data.entity.DishEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserAccountEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;



    private String username;

    private String password;

    private String email;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<AuthorityEntity> authorities;

    private Boolean enabled;
    private Date lastPasswordResetDate;

    public UserAccountEntity(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    UserAccountEntity() {
        // jpa empty constructor
    }


    public Long getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
}