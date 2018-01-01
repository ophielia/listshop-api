package com.meg.atable.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by margaretmartin on 01/01/2018.
 */
@Entity
@Table(name = "target_proposal")
public class TargetProposalEntity {

    @Id
    @GeneratedValue
    private Long targetProposalId;
}
