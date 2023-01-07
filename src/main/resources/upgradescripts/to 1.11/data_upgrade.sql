update list_category set display_order = 100 where category_id = 6;
update list_category set display_order = 200 where category_id = 5;
update list_category set display_order = 300 where category_id = 7;
update list_category set display_order = 400 where category_id = 8;
update list_category set display_order = 500 where category_id = 9;
update list_category set display_order = 600 where category_id = 10;
update list_category set display_order = 700 where category_id = 52010;


update list set list_layout_id = null;

--CHECK
select * from list_item where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
select * from category_tags where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
select * from tag_relation where parent_tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
--select * from tag_relation where child_tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
--select * from tag where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );

-- lets do this - get rid of the duplicates!!
select * from list_item where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
select * from category_tags where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
select * from tag_relation where parent_tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
delete * from tag_relation where child_tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
delete * from tag where tag_id in (51034 ,51155 ,51041 ,51055 ,50731 ,51126 ,51128 ,50955 ,50836 ,51073 );
