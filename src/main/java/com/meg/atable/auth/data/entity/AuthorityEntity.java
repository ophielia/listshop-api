package com.meg.atable.auth.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "AUTHORITY")
public class AuthorityEntity {

    @Id
    @Column(name = "authority_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq")
    @SequenceGenerator(name = "authority_seq", sequenceName = "authority_seq", allocationSize = 1)
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthorityName name;


    @ManyToOne
    @JoinColumn(name="user_id")
    private UserAccountEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

   /* public List<UserAccountEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserAccountEntity> users) {
        this.users = users;
    }
*/
}