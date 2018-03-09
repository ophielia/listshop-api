-- base test user - id 1
insert into users (email, enabled, last_password_reset_date, password, username, user_id) values (null, true,null, 'password', 'testuser', 500);
insert into users (email, enabled, last_password_reset_date, password, username, user_id) values (null, true,null, 'password', 'adduser', 501);

-- tags - ids 500-504
insert into tag (assign_select, auto_tag_flag, description, is_parent_tag, is_verified, name, power, rating_family, search_select, tag_type, tag_type_default, tag_id) values (true, null, null, false, true, 'tag1', 0, null, true, 'TagType', false, 500);
insert into tag (assign_select, auto_tag_flag, description, is_parent_tag, is_verified, name, power, rating_family, search_select, tag_type, tag_type_default, tag_id) values (true, null, null, false, true, 'tag2', 0, null, true, 'TagType', false, 501);
insert into tag (assign_select, auto_tag_flag, description, is_parent_tag, is_verified, name, power, rating_family, search_select, tag_type, tag_type_default, tag_id) values (true, null, null, false, true, 'tag3', 0, null, true, 'TagType', false, 502);
insert into tag (assign_select, auto_tag_flag, description, is_parent_tag, is_verified, name, power, rating_family, search_select, tag_type, tag_type_default, tag_id) values (true, null, null, false, true, 'tag4', 0, null, true, 'TagType', false, 503);
insert into tag (assign_select, auto_tag_flag, description, is_parent_tag, is_verified, name, power, rating_family, search_select, tag_type, tag_type_default, tag_id) values (true, null, null, false, true, 'tag5', 0, null, true, 'TagType', false, 504);


-- lists - ids 500-503
insert into list (created_on, list_layout_type, list_types, user_id, list_id) values (current_timestamp(), 'All', 'BaseList' , 500, 500);  -- base list - id 500
insert into list (created_on, list_layout_type, list_types, user_id, list_id) values (current_timestamp(),'All', 'ActiveList',  500, 501); -- active list - id 501
insert into list (created_on, list_layout_type, list_types, user_id, list_id) values (current_timestamp(),'All', 'InProcess', 500, 502);  -- list to be deleted - id 502

-- list items - one item, for active list - id 500
insert into list_item (added_on, crossed_off, free_text, source, list_category, list_id, tag_id, used_count, item_id) 
values (current_timestamp(), current_timestamp(), null, null, null, 500, 500, 1, 500); 

-- dishes - 3 - ids 500-503
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id) values (null, null, 'dish1', null, 500, 500);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id) values (null, null, 'dish2', null, 500, 501);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id) values (null, null, 'dish3', null, 500, 502);
insert into dish_tags (dish_id, tag_id) values (500, 500);
insert into dish_tags (dish_id, tag_id) values (500, 501);
insert into dish_tags (dish_id, tag_id) values (501, 501);
insert into dish_tags (dish_id, tag_id) values (501, 502);
insert into dish_tags (dish_id, tag_id) values (502, 503);

-- meal plan - 1 - id 500
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id) values (current_timestamp(), 'Manual', 'meal plan 1', 500, 500) ;

-- slots for meal plan - 
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (500, 500, 500);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (501, 500, 501);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (502, 500, 502);
