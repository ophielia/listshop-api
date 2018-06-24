-- add column to meal plan
ALTER TABLE meal_plan ADD COLUMN   target_id      bigint        ;

-- proposal_context changes
ALTER TABLE proposal_context DROP COLUMN  approach_type ;
ALTER TABLE proposal_context DROP COLUMN  dish_count_per_slot;
ALTER TABLE proposal_context DROP COLUMN  maximum_empties;
ALTER TABLE proposal_context DROP COLUMN  proposal_count               ;
ALTER TABLE proposal_context DROP COLUMN  refresh_flag;

ALTER TABLE proposal_context ADD COLUMN   current_approach_type   character varying(255)   ;
ALTER TABLE proposal_context ADD COLUMN   current_approach_index   integer   ;
ALTER TABLE proposal_context ADD COLUMN   meal_plan_id   bigint   ;
ALTER TABLE proposal_context ADD COLUMN   target_id      bigint        ;
ALTER TABLE proposal_context ADD COLUMN   target_hash_code   character varying(255)    ;
ALTER TABLE proposal_context ADD COLUMN   proposal_hash_code character varying(255)     ;

-- proposal_approach
CREATE TABLE proposal_approach (
    proposal_approach_id bigint NOT NULL,
    proposal_context_id bigint NOT NULL,
    approach_number int,
    instructions character varying(255)
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
    slot_id bigint not null,
    dish_id bigint,
    matched_tag_ids character varying(255)
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
    flat_matched_tag_ids character varying(255),
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
    is_refreshable boolean,
    created timestamp 

);

CREATE SEQUENCE proposal_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

alter table target drop column target;
alter table target add column target character varying(255);
alter table target_slot drop column target;
alter table target_slot add column target character varying(255);
-- old tables
--drop table target_proposal_dish;
--drop table target_proposal_slot;
--drop table target_proposal;

