package com.meg.listshop.lmt.api.model;

/**
 * Created by margaretmartin on 29/10/2017.
 */
public class ItemSource {

private Long id;

private String display;

private String type;

    public ItemSource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
