package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "campaign")
@GenericGenerator(
        name = "campaign_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "campaign_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "1000"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)

public class CampaignEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campaign_sequence")
    @Column(name = "campaign_id")
    private Long campaign_id;

    private String campaign;
    private String email;
    private Date created;

    public CampaignEntity(String campaign, String email) {
        this.campaign = campaign;
        this.email = email;
        this.created = new Date();
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
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
                ", created=" + created +
                '}';
    }
}