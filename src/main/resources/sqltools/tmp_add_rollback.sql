-- proposal_context changes
ALTER TABLE proposal_context add COLUMN  approach_type character varying(255);
ALTER TABLE proposal_context add COLUMN  dish_count_per_slot integer;
ALTER TABLE proposal_context add COLUMN  maximum_empties integer;
ALTER TABLE proposal_context add COLUMN  proposal_count       integer        ;
ALTER TABLE proposal_context add COLUMN  refresh_flag character varying(255);

ALTER TABLE proposal_context drop COLUMN   currentApproachType;
ALTER TABLE proposal_context drop COLUMN   targetId              ;
ALTER TABLE proposal_context drop COLUMN   targetHashCode    ;
ALTER TABLE proposal_context drop COLUMN   proposalHashCode      ;


CREATE TABLE proposal_context (
    proposal_context_id bigint NOT NULL,
    approach_type character varying(255),
    dish_count_per_slot integer,
    maximum_empties integer,
    proposal_count integer,
    proposal_id bigint,
    refresh_flag character varying(255),
    current_attempt_index integer
);

-- proposal_approach
drop TABLE proposal_approach;


drop SEQUENCE proposal_approach_sequence;

-- proposal_dish

drop TABLE proposal_dish ;

drop SEQUENCE proposal_dish_sequence;;


-- proposal_slot

drop TABLE proposal_slot;

drop SEQUENCE proposal_slot_sequence;


drop TABLE proposal;

drop SEQUENCE proposal_sequence;

-- old tables
--drop table target_proposal_dish;
--drop table target_proposal_slot;
--drop table target_proposal;
