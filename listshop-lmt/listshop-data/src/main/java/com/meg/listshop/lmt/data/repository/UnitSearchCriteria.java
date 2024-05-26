package com.meg.listshop.lmt.data.repository;

import com.meg.listshop.conversion.data.pojo.DomainType;

public class UnitSearchCriteria {

    private Boolean isLiquid;
    private DomainType domainType;

    public UnitSearchCriteria(Boolean isLiquid, DomainType domainType) {
        this.isLiquid = isLiquid;
        this.domainType = domainType;
    }

    public Boolean getLiquid() {
        return isLiquid;
    }

    public DomainType getDomainType() {
        return domainType;
    }
}