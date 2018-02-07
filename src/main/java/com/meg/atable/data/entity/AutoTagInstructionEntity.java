package com.meg.atable.data.entity;

import com.meg.atable.api.model.ListLayoutType;

import javax.persistence.*;
import java.util.List;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Instruction_Type")
@Table(name = "auto_tag_instructions")
@SequenceGenerator(name="auto_tag_instructions_sequence", sequenceName = "auto_tag_instructions_sequence")
public class AutoTagInstructionEntity {
    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="auto_tag_instructions_sequence")
    @Column(name = "instruction_id")
    private Long instructionId;


    private String searchTerms;

    private Boolean isInvert;

    private Long assignTagId;

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
}
