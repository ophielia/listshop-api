-- is display will be for default tag that deleted tags without substitute are assigned to.
ALTER TABLE tag ADD COLUMN is_display  boolean DEFAULT true;
ALTER TABLE tag ADD COLUMN to_delete  boolean DEFAULT false;
ALTER TABLE tag ADD COLUMN replacement_tag_id  bigint;

update tag set is_display = true;
update tag set to_delete = false;

ALTER TABLE tag ALTER COLUMN is_display SET NOT NULL;


ALTER TABLE tag ALTER COLUMN to_delete SET NOT NULL;

--    target_proposal
--    target_proposal_dish
--    target_proposal_slot
--    target_tags
--    proposal_context_slot
drop table proposal_context_slot;
drop table target_proposal;
drop table target_proposal_dish;
drop table target_proposal_slot;
drop table target_tags;
-- possible deprecation
--table
--    proposal_context_slot
--    target_proposal
--    target_proposal_dish
--    target_proposal_slot
--    target_tags

-- table change name
-- proposal_dish => proposal_dish_slot




-- undo section
--ALTER TABLE tag drop COLUMN is_display;
--ALTER TABLE tag drop COLUMN to_delete;
--ALTER TABLE tag drop COLUMN replacement_tag_id;
