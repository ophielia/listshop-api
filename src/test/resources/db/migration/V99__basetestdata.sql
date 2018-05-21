-- base test user - id 1
insert into users (email, enabled, last_password_reset_date, password, username, user_id) values (null, true,null, 'password', 'testuser', 500);
insert into users (email, enabled, last_password_reset_date, password, username, user_id) values (null, true,null, 'password', 'adduser', 501);

-- tags - ids 500-504
insert into tag (assign_select,  description,  is_verified, name, power, search_select, tag_type, tag_type_default, tag_id) values (true, null,  true, 'tag2', 0, true, 'TagType', false, 501);
insert into tag (assign_select,  description,  is_verified, name, power, search_select, tag_type, tag_type_default, tag_id) values (true, null,  true, 'tag3', 0, true, 'TagType', false, 502);
insert into tag (assign_select,  description,  is_verified, name, power, search_select, tag_type, tag_type_default, tag_id) values (true, null,  true, 'tag4', 0, true, 'TagType', false, 503);
insert into tag (assign_select,  description,  is_verified, name, power, search_select, tag_type, tag_type_default, tag_id) values (true, null,  true, 'tag1', 0, true, 'TagType', false, 500);
insert into tag (assign_select,  description,  is_verified, name, power, search_select, tag_type, tag_type_default, tag_id) values (true, null,  true, 'tag5', 0, true, 'TagType', false, 504);


-- lists - ids 500-503
insert into list (created_on, list_layout_id, list_types, user_id, list_id) values (current_timestamp(), 1, 'BaseList' , 500, 500);  -- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, list_id) values (current_timestamp(),11, 'ActiveList',  20, 501); -- active list - id 501
insert into list (created_on, list_layout_id, list_types, user_id, list_id) values (current_timestamp(),11, 'ActiveList', 500, 502);  -- list to be deleted - id 502

-- list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (500,  501, 500,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (500,  502, 501,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (500,  503, 502,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (500,  500, 503,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (500,  504, 504,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  16, 505,current_timestamp(), null, null,    1,  '16;90', null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  18, 506,current_timestamp(), null, null,    1,  null, null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  21, 507,current_timestamp(), null, null,    1,  '90', null);
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  359, 508,current_timestamp(), null, null,    1,  null, 'PickUpList');
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  470, 509,current_timestamp(), null, null,    1,  null, 'BaseList');

INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  210, 510,current_timestamp(), null, null,    1,  null, 'BaseList');
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  211, 511,current_timestamp(), null, null,    1,  null, 'BaseList');
INSERT INTO list_item(list_id, tag_id, item_id,added_on, crossed_off, free_text,    used_count,  dish_sources, list_sources)
	VALUES (501,  113, 512,current_timestamp(), null, null,    2,  ';83;', 'BaseList');


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
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id) values (current_timestamp(), 'Manual', 'meal plan 1', 501, 501) ;
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id) values (current_timestamp(), 'Manual', 'meal plan 1', 20, 503) ;

-- slots for meal plan - 
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (500, 500, 500);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (501, 500, 501);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (502, 500, 502);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (500, 501, 503);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (501, 501, 504);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (502, 501, 505);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (500, 503, 506);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (501, 503, 507);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id) values (502, 503, 508);



-- list layouts - ids 999 - to delete
INSERT INTO list_layout (layout_id, layout_type, name) VALUES (999, 'RoughGrained', 'ToDelete');

-- list categories - new - for testing adding - ids 501, 502, 503
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (500, 'One', 5, 1);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (501, 'Two', 5, 3);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (502, 'Three', 5, 5);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (503, 'Four', 5, 7);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (504, 'Parent', 5, 9);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (505, 'FirstChild', 5, 11);
INSERT INTO list_category (category_id, name, layout_id, display_order) VALUES (506, 'SecondChild', 5, 12);

-- list layout subcategories
INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (500, 1, 2);
INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (501, 3, 2);
INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (502, 8, 7);
 INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (503, 9, 7);
  INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (504, 505, 504);
  INSERT INTO category_relation (category_relation_id, child_category_id, parent_category_id)
 VALUES (505, 506, 504);

-- category_tags
INSERT INTO category_tags (category_id, tag_id) VALUES (3, 500);
INSERT INTO category_tags (category_id, tag_id) VALUES (1, 501);
INSERT INTO category_tags (category_id, tag_id) VALUES (1, 502);
INSERT INTO category_tags (category_id, tag_id) VALUES (2, 503);
INSERT INTO category_tags (category_id, tag_id) VALUES (501, 500);
INSERT INTO category_tags (category_id, tag_id) VALUES (501, 500);
