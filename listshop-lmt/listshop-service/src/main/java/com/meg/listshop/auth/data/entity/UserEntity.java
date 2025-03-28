package com.meg.listshop.auth.data.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence")
    @SequenceGenerator(name = "user_id_sequence", sequenceName = "user_id_sequence", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;


    private String username;

    private String password;

    private String email;

    private Date creationDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastLogin;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<AuthorityEntity> authorities;

    private Boolean enabled;
    private Date lastPasswordResetDate;

    public UserEntity(String userName, String password) {
        this.email = userName;
        this.username = userName;
        this.password = password;
    }

    public UserEntity(String userName, String email, String password) {
        this.username = userName;
        this.password = password;
        this.email = email;
        this.enabled = true;
    }

    public UserEntity() {
        // jpa empty constructor
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", creationDate=" + creationDate +
                ", lastLogin=" + lastLogin +
                ", authorities=" + authorities +
                ", enabled=" + enabled +
                ", lastPasswordResetDate=" + lastPasswordResetDate +
                '}';
    }
}