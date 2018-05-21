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




-- ====================
-- ======= data changes
-- ====================

