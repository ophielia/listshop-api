-- is display will be for default tag that deleted tags without substitute are assigned to.
ALTER TABLE tag ADD COLUMN is_display  boolean DEFAULT true;
ALTER TABLE tag ADD COLUMN to_delete  boolean DEFAULT false;
ALTER TABLE tag ADD COLUMN replacement_tag_id  bigint;

update tag set is_display = true;
update tag set to_delete = false;

ALTER TABLE tag ALTER COLUMN is_display SET NOT NULL;


ALTER TABLE tag ALTER COLUMN to_delete SET NOT NULL;




-- undo section
--ALTER TABLE tag drop COLUMN is_display;
--ALTER TABLE tag drop COLUMN to_delete;
--ALTER TABLE tag drop COLUMN replacement_tag_id;
