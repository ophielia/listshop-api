-- proposal_context changes
ALTER TABLE proposal_context DROP COLUMN  approach_type character;
ALTER TABLE proposal_context DROP COLUMN  dish_count_per_slot;
ALTER TABLE proposal_context DROP COLUMN  maximum_empties;
ALTER TABLE proposal_context DROP COLUMN  proposal_count               ;
ALTER TABLE proposal_context DROP COLUMN  refresh_flag;

ALTER TABLE proposal_context ADD COLUMN   currentApproachType   character varying(255)   ;
ALTER TABLE proposal_context ADD COLUMN   targetId      bigint        ;
ALTER TABLE proposal_context ADD COLUMN   targetHashCode   character varying(255)    ;
ALTER TABLE proposal_context ADD COLUMN   proposalHashCode character varying(255)     ;

-- proposal_approach
CREATE TABLE proposal_approach (
    proposal_approach_id bigint NOT NULL,
    approach_number int,
    instructions varying(255)
);


CREATE SEQUENCE proposal_approach_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- proposal_dish

CREATE TABLE proposal_dish (
    dish_slot_id bigint NOT NULL,
    dish_id bigint,
    matched_tag_ids varying(255)
);

CREATE SEQUENCE proposal_dish_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- proposal_slot

CREATE TABLE proposal_slot (
    slot_id bigint NOT NULL,
    slot_number integer,
    flat_matched_tag_ids varying(255),
    proposal_id bigint NOT NULL,
    picked_dish_id bigint,
    slot_dish_tag_id bigint

);

CREATE SEQUENCE proposal_slot_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE proposal (
    proposal_id bigint NOT NULL,
    user_id bigint,
    is_refreshable boolean

);

CREATE SEQUENCE proposal_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- old tables
drop table target_proposal_dish;
drop table target_proposal_slot;
drop table target_proposal;

