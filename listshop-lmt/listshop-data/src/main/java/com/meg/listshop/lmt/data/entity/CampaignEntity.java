package com.meg.listshop.lmt.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "campaigns")
public class CampaignEntity {


    @Id
    @Tsid
    @Column(name = "campaign_id")
    private Long campaign_id;

    private String campaign;
    private String email;
    private Date created_on;

    public CampaignEntity(String campaign, String email) {
        this.campaign = campaign;
        this.email = email;
        this.created_on = new Date();
    }

    public CampaignEntity() {
        // jpa empty constructor
    }

    public Long getId() {
        return campaign_id;
    }

    public void setId(Long campaignId) {
        this.campaign_id = campaignId;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created_on;
    }

    public void setCreated(Date created) {
        this.created_on = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignEntity that = (CampaignEntity) o;
        return Objects.equals(campaign_id, that.campaign_id) && Objects.equals(campaign, that.campaign) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campaign_id, campaign, email);
    }

    @Override
    public String toString() {
        return "CampaignEntity{" +
                "campaign_id=" + campaign_id +
                ", campaign='" + campaign + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created_on +
                '}';
    }
}