package com.meg.listshop.lmt.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CampaignPut {

    @JsonProperty("campaign")
    @NotEmpty()
    @Size(max = 20, min = 3, message = "campaign field should be between 7 and 100 chars.")
    private String campaign;

    @JsonProperty("email")
    @NotEmpty()
    @Size(max = 100, min = 7, message = "email field should be between 7 and 100 chars.")
    private String email;

    public String getCampaign() {
        return campaign;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "CampaignPut{" +
                "campaign='" + campaign + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}