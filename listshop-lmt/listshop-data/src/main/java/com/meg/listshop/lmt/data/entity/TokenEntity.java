package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.TokenType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "tokens")
public class TokenEntity {

    @Column(name = "user_id")
    private Long userId;

    @Id
    @Tsid
    @Column(name = "token_id")
    private Long token_id;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "token_value")
    private String tokenValue;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}