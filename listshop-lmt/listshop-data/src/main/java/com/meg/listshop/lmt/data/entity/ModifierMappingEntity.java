package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "modifier_mappings")
@GenericGenerator(
        name = "modifier_mapping_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "modifier_mapping_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class ModifierMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modifier_mapping_sequence")
    @Column(name = "mapping_id")
    private Long mappingId;
    @Column(name = "modifier_type")
    private String modifierType;
    private String modifier;
    @Column(name = "mapped_modifier")
    private String mappedModifier;

    public ModifierMappingEntity() {
        // empty constructor
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public String getModifierType() {
        return modifierType;
    }

    public void setModifierType(String modifierType) {
        this.modifierType = modifierType;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getMappedModifier() {
        return mappedModifier;
    }

    public void setMappedModifier(String mappedModifier) {
        this.mappedModifier = mappedModifier;
    }

    @Override
    public String toString() {
        return "ModifierMappingEntity{" +
                "mappingId=" + mappingId +
                ", modifierType='" + modifierType + '\'' +
                ", modifier='" + modifier + '\'' +
                ", mappedModifier='" + mappedModifier + '\'' +
                '}';
    }
}
