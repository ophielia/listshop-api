package com.meg.listshop.auth.data.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "admin_user_details")
@Immutable
public class AdminUserDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    private String email;
    private String userName;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "last_login")
    private Date lastLogin;
    @Column(name = "list_count")
    private long listCount;
    @Column(name = "dish_count")
    private long dishCount;
    @Column(name = "meal_plan_count")
    private long mealPlanCount;

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public long getListCount() {
        return listCount;
    }

    public long getDishCount() {
        return dishCount;
    }

    public long getMealPlanCount() {
        return mealPlanCount;
    }

    @Override
    public String toString() {
        return "AdminUserDetailsEntity{" +
                "user_id=" + userId +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", creationDate=" + creationDate +
                ", lastLogin=" + lastLogin +
                ", listCount=" + listCount +
                ", dishCount=" + dishCount +
                ", mealPlanCount=" + mealPlanCount +
                '}';
    }
}