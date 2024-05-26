package com.meg.listshop.common.data.entity;

import com.meg.listshop.common.UnitSubtype;
import com.meg.listshop.common.UnitType;

import javax.persistence.*;

@Entity
@Table(name = "units")
public class UnitEntity {

    @Id
    @Column(name = "UNIT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UnitType type;

    @Enumerated(EnumType.STRING)
    private UnitSubtype subtype;

    @Column(name = "name")
    private String name;

    @Column(name = "IS_LIQUID")
    private boolean isLiquid;

    @Column(name = "IS_LIST_UNIT")
    private boolean isListUnit;

    @Column(name = "IS_DISH_UNIT")
    private boolean isDishUnit;

    @Column(name = "IS_WEIGHT")
    private boolean isWeight;

    @Column(name = "IS_VOLUME")
    private boolean isVolume;

    @Column(name = "IS_TAG_SPECIFIC")
    private boolean isTagSpecific;

    @Column(name = "EXCLUDED_DOMAINS")
    private String excludedDomainList;

    @Column(name = "ONE_WAY_CONVERSION")
    private Boolean oneWayConversion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public boolean isLiquid() {
        return isLiquid;
    }

    public void setLiquid(boolean liquid) {
        isLiquid = liquid;
    }

    public boolean isListUnit() {
        return isListUnit;
    }

    public void setListUnit(boolean listUnit) {
        isListUnit = listUnit;
    }

    public boolean isDishUnit() {
        return isDishUnit;
    }

    public void setDishUnit(boolean dishUnit) {
        isDishUnit = dishUnit;
    }

    public boolean isWeight() {
        return isWeight;
    }

    public void setWeight(boolean weight) {
        isWeight = weight;
    }

    public boolean isVolume() {
        return isVolume;
    }

    public void setVolume(boolean volume) {
        isVolume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public UnitSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(UnitSubtype subtype) {
        this.subtype = subtype;
    }

    public boolean isTagSpecific() {
        return isTagSpecific;
    }

    public void setTagSpecific(boolean tagSpecific) {
        isTagSpecific = tagSpecific;
    }

    public String getExcludedDomainList() {
        return excludedDomainList;
    }

    public void setExcludedDomainList(String excludedDomainList) {
        this.excludedDomainList = excludedDomainList;
    }

    public Boolean getOneWayConversion() {
        return oneWayConversion;
    }

    public void setOneWayConversion(Boolean oneWayConversion) {
        this.oneWayConversion = oneWayConversion;
    }

    public boolean isAvailableForDomain(UnitType domain) {
        if (excludedDomainList == null || excludedDomainList.isEmpty()) {
            return true;
        }
        return !excludedDomainList.contains(domain.toString());
    }

    public boolean isOneWayConversion() {
        if (oneWayConversion == null) {
            return false;
        }
        return oneWayConversion;
    }

    @Override
    public String toString() {
        return "UnitEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", subtype=" + subtype +
                ", isListUnit=" + isListUnit +
                ", isDishUnit=" + isDishUnit +
                ", isLiquid=" + isLiquid +
                ", isWeight=" + isWeight +
                ", isVolume=" + isVolume +
                ", isTagSpecific=" + isTagSpecific +
                '}';
    }
}
