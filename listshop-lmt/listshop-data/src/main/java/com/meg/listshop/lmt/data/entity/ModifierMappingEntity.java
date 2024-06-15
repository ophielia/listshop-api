package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.ModifierType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "modifier_mappings")
@NamedNativeQueries({
        @NamedNativeQuery(name = "NonUnitSuggestions",
                query = "select distinct modifier_type, modifier , reference_id , length(trim(modifier)) as discard from modifier_mappings " +
                        " where modifier_type <> 'Unit'" +
                        " order by length(trim(modifier)) " ,
                resultSetMapping = "Mapping.SuggestionDTO"),
        @NamedNativeQuery(name = "UnitSuggestions",
                query = "select distinct modifier_type, modifier , reference_id, length(trim(modifier)) as discard  from modifier_mappings " +
                        " where modifier_type = 'Unit' and reference_id in (:unitIds)" +
                        " order by length(trim(modifier)) " ,
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
