package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.ModifierType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "modifier_mappings")
@NamedNativeQueries({
        @NamedNativeQuery(name = "NonUnitSuggestions",
                query = "select distinct modifier_type, modifier , reference_id , mapping_id as discard from modifier_mappings " +
                        " where modifier_type <> 'Unit'" +
                        " order by mapping_id ",
                resultSetMapping = "Mapping.SuggestionDTO"),
        @NamedNativeQuery(name = "UnitSuggestions",
                query = "select distinct modifier_type, modifier , reference_id, mapping_id as discard  from modifier_mappings " +
                        " where modifier_type = 'Unit' and reference_id in (:unitIds)" +
                        " order by mapping_id ",
                resultSetMapping = "Mapping.SuggestionDTO")
})
@SqlResultSetMapping(
        name = "Mapping.SuggestionDTO",
        classes = {
                @ConstructorResult(
                        targetClass = com.meg.listshop.lmt.data.pojos.SuggestionDTO.class,
                        columns = {
                                @ColumnResult(name = "modifier_type", type = String.class),
                                @ColumnResult(name = "modifier", type = String.class),
                                @ColumnResult(name = "reference_id", type = Long.class),
                                @ColumnResult(name = "discard", type = Long.class)
                        })})

public class ModifierMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modifier_mapping_sequence")
    @SequenceGenerator(name = "modifier_mapping_sequence", sequenceName = "modifier_mapping_sequence", allocationSize = 1)
    @Column(name = "mapping_id")
    private Long mappingId;
    @Enumerated(EnumType.STRING)
    @Column(name = "modifier_type")
    private ModifierType modifierType;
    private String modifier;
    @Column(name = "mapped_modifier")
    private String mappedModifier;
    @Column(name = "reference_id")
    private Long referenceId;

    public ModifierMappingEntity() {
        // empty constructor
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public ModifierType getModifierType() {
        return modifierType;
    }

    public void setModifierType(ModifierType modifierType) {
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
