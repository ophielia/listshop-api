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
    @Size(max = 100, message = "email field should be between 7 and 100 chars.")
    private String email;

    @JsonProperty("text")
    @NotEmpty()
    @Size(max = 500, message = "message field should be no more than 500 chars.")
    private String text;

    public String getCampaign() {
        return campaign;
    }

    public String getEmail() {
        return email;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "CampaignPut{" +
                "campaign='" + campaign + '\'' +
                ", email='" + email + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}