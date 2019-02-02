-- is display will be for default tag that deleted tags without substitute are assigned to.
ALTER TABLE tag ADD COLUMN to_delete  boolean DEFAULT false;
ALTER TABLE tag ADD COLUMN replacement_tag_id  bigint;

update tag set to_delete = false;

ALTER TABLE tag ALTER COLUMN to_delete SET NOT NULL;

drop table proposal_context_slot;
drop table target_proposal;
drop table target_proposal_dish;
drop table target_proposal_slot;
drop table target_tags;


-- table change name
-- proposal_dish => proposal_dish_slot




-- undo section
--ALTER TABLE tag drop COLUMN is_display;
--ALTER TABLE tag drop COLUMN to_delete;
--ALTER TABLE tag drop COLUMN replacement_tag_id;
