package com.meg.atable.data.entity;

/**
 * Created by margaretmartin on 23/05/2018.
 */
public class ContextApproachEntity {

    // MM WOW do we need some clean up here!
    private Integer approachNumber;
    private ProposalContextEntity context;
    private String instructions;

    public Integer getApproachNumber() {
        return approachNumber;
    }

    public void setApproachNumber(Integer approachNumber) {
        this.approachNumber = approachNumber;
    }

    public void setContext(ProposalContextEntity context) {
        this.context = context;
    }

    public ProposalContextEntity getContext() {
        return context;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
