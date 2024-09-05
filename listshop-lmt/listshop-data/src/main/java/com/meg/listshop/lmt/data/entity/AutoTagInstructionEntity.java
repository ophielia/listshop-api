package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Instruction_Type")
@Table(name = "auto_tag_instructions")
@GenericGenerator(
        name = "auto_tag_instructions_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="auto_tag_instructions_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value="1000"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class AutoTagInstructionEntity {
    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="auto_tag_instructions_sequence")
    @Column(name = "instruction_id")
    private Long instructionId;


    private String searchTerms;

    private Boolean isInvert;

    private Long assignTagId;

    private String invertFilter;

    public Long getId() {
        return instructionId;
    }

    public void setId(Long instructionId) {
        this.instructionId = instructionId;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public Boolean getInvert() {
        return isInvert;
    }

    public void setInvert(Boolean invert) {
        isInvert = invert;
    }

    public Long getAssignTagId() {
        return assignTagId;
    }

    public void setAssignTagId(Long assignTagId) {
        this.assignTagId = assignTagId;
    }

    public String getInvertFilter() {
        return invertFilter;
    }

    public void setInvertFilter(String invertFilter) {
        this.invertFilter = invertFilter;
    }
}
