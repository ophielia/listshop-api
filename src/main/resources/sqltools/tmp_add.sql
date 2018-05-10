ALTER TABLE public.list
ADD COLUMN meal_plan_id bigint;

DROP TABLE TARGET_SLOTS;

CREATE SEQUENCE target_slot_sequence
    START WITH 1000
    INCREMENT BY 2
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE list
DROP COLUMN list_layout_type;

ALTER TABLE list
DROP COLUMN list_type;

ALTER TABLE tag
DROP COLUMN rating_family;

ALTER TABLE tag
DROP COLUMN is_parent_tag;

ALTER TABLE tag
DROP COLUMN auto_tag_flag;






=========================
ALTER TABLE public.list
DROP COLUMN meal_plan_id;

CREATE TABLE target_slots (
    target_entity_target_id bigint NOT NULL,
    slots_target_slot_id bigint NOT NULL
);

DROP SEQUENCE target_slot_sequence;

ALTER TABLE list
ADD COLUMN list_layout_type varchar(255);

ALTER TABLE list
ADD COLUMN list_type integer;

ALTER TABLE tag
ADD COLUMN rating_family varchar(256);

ALTER TABLE tag
ADD COLUMN is_parent_tag boolean;

ALTER TABLE tag
ADD COLUMN auto_tag_flag integer;
