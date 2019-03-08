-- mass correction of test - data for new columns
update tag set is_display = true;
update tag set to_delete = false;

update tag set is_display = false where tag_id = 505;
update tag set is_display = true where tag_id = 1000;
update tag set to_delete = true where tag_id = 506;

