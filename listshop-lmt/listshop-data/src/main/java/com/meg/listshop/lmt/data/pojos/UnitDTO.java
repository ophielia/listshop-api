package com.meg.listshop.lmt.data.pojos;

import java.util.Objects;


public class UnitDTO {

    private Long unitId;
    private String display;

    public UnitDTO() {
        // cause jackson likes it this way....
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "UnitDTO{" +
                "unitId=" + unitId +
                ", display='" + display + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitDTO unitDTO = (UnitDTO) o;
        return Objects.equals(unitId, unitDTO.unitId) && Objects.equals(display, unitDTO.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitId, display);
    }
}