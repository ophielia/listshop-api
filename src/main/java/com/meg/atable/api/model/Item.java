package com.meg.atable.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meg.atable.data.entity.TagEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class Item {

    private Long item_id;

    private TagEntity tag;

    @JsonProperty("item_source")
    private ItemSourceType itemSource;

    @JsonProperty("added")
    private Date addedOn;

    @JsonProperty("free_text")
    private String freeText;

    @JsonProperty("crossed_off")
    private Date crossedOff;

    //MM need this?? private String listCategory;
}
