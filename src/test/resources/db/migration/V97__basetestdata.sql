-- base test user - id 1
insert into users (email, enabled, last_password_reset_date, password, username, user_id)
values ('testuser', true, null, 'password', 'testuser', 500);
insert into users (email, enabled, last_password_reset_date, password, username, user_id)
values ('adduser', true, null, 'password', 'adduser', 501);
insert into users (email, enabled, last_password_reset_date, password, username, user_id)
values ('deleteuser', true, null, 'password', 'deleteuser', 502);

-- tags - ids 500-504
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id)
values (true, null, true, 'tag2', 0, true, 'TagType', false, 501);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id)
values (true, null, true, 'tag3', 0, true, 'TagType', false, 502);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id)
values (true, null, true, 'tag4', 0, true, 'TagType', false, 503);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id)
values (true, null, true, 'tag1', 0, true, 'TagType', false, 500);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id)
values (true, null, true, 'tag5', 0, true, 'TagType', false, 504);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id, to_delete)
values (true, null, true, 'notdisplayed', 0, true, 'Ingredient', false, 505, false);
insert into tag (assign_select, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id, to_delete)
values (true, null, true, 'notdisplayed', 0, true, 'Ingredient', false, 506, true);


-- assigns no display tag to tag in heirarchy (which contained other tags)
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (9000, 505, 393);
-- assigns no display tag to tag in heirarchy (which contained other tags)
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (9001, 506, 393);


-- lists - ids 500-503
-- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, name, list_id)
values (now(), 1, 'BaseList', 500, 'list3', 500);
-- active list - id 501
insert into list (created_on, list_layout_id, list_types, user_id, name, list_id)
values (now(), 11, 'ActiveList', 20, 'list2', 501);
-- list to be deleted - id 502
insert into list (created_on, list_layout_id, list_types, user_id, name, list_id)
values (now(), 11, 'ActiveList', 500, 'list1', 502);
insert into list (created_on, list_layout_id, list_types, user_id, name, list_id)
values (now(), 11, 'ActiveList', 20, 'list1', 402);

-- list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500, 501, 500, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500, 502, 501, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500, 503, 502, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500, 500, 503, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500, 504, 504, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 16, 505, now(), null, null, 1, '16;90', null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 18, 506, now(), null, null, 1, '54;55', null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 21, 507, now(), null, null, 1, '90', null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 359, 508, now(), null, null, 1, '55;56', '501');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 470, 509, now(), null, null, 1, null, '402');

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 210, 510, now(), null, null, 1, null, '402');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 211, 511, now(), null, null, 1, null, '402');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (501, 113, 512, now(), null, null, 2, ';83;', null);


-- dishes - 3 - ids 500-503
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish1', null, 500, 500);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish2', null, 500, 501);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish3', null, 500, 502);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish4', null, 500, 503);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish4', null, 500, 603);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish4', null, 500, 504);
insert into dish_tags (dish_id, tag_id)
values (500, 500);
insert into dish_tags (dish_id, tag_id)
values (500, 501);
insert into dish_tags (dish_id, tag_id)
values (501, 501);
insert into dish_tags (dish_id, tag_id)
values (501, 502);
insert into dish_tags (dish_id, tag_id)
values (502, 503);
-- inserting tags for delete tag test - tags 110 and 353 into dish 54
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (54, 110);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (54, 353);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 353);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 218);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 363);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 419);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 344);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 322);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 396);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 426);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (503, 400);

INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 353);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 218);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 363);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 419);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 344);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 322);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 396);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 426);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (603, 400);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 353);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 218);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 363);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 419);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 344);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 322);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 396);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 426);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (62, 400);


-- meal plan - 1 - id 500
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 1', 500, 500);
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 2', 500, 501);
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 1', 20, 503);
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 2', 20, 504);
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 2', 20, 506);
insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 5', 500, 505);

-- slots for meal plan - 
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (500, 500, 500);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (501, 500, 501);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (502, 500, 502);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (500, 501, 503);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (501, 501, 504);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (502, 501, 505);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (500, 503, 506);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (501, 503, 507);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (502, 503, 508);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (500, 504, 509);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (501, 504, 510);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (502, 504, 511);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (503, 505, 512);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (504, 505, 513);


-- list layouts - ids 999 - to delete
INSERT INTO list_layout (layout_id, layout_type, name)
VALUES (999, 'RoughGrained', 'ToDelete');

-- list categories - new - for testing adding - ids 501, 502, 503
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (500, 'One', 5, 1);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (501, 'Two', 5, 3);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (502, 'Three', 5, 5);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (503, 'Four', 5, 7);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (504, 'Parent', 5, 9);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (505, 'FirstChild', 5, 11);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (506, 'SecondChild', 5, 12);

INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (510, 'One', 11, 1);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (511, 'Two', 11, 3);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (512, 'Three', 11, 5);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (513, 'Four', 11, 7);
INSERT INTO list_category (category_id, name, layout_id, display_order)
VALUES (514, 'Parent', 11, 9);


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
INSERT INTO category_tags (category_id, tag_id)
VALUES (3, 500);
INSERT INTO category_tags (category_id, tag_id)
VALUES (1, 501);
INSERT INTO category_tags (category_id, tag_id)
VALUES (1, 502);
INSERT INTO category_tags (category_id, tag_id)
VALUES (2, 503);
INSERT INTO category_tags (category_id, tag_id)
VALUES (501, 500);
INSERT INTO category_tags (category_id, tag_id)
VALUES (501, 500);
INSERT INTO category_tags (category_id, tag_id)
VALUES (501, 505);

INSERT INTO category_tags (category_id, tag_id)
VALUES (510, 500);
INSERT INTO category_tags (category_id, tag_id)
VALUES (511, 501);
INSERT INTO category_tags (category_id, tag_id)
VALUES (512, 502);
INSERT INTO category_tags (category_id, tag_id)
VALUES (513, 503);
INSERT INTO category_tags (category_id, tag_id)
VALUES (514, 504);

-- targets
INSERT INTO target (target, target_type, target_id, created, last_updated, last_used, target_name, target_tag_ids,
                    user_id)
VALUES ('TargetEntity', 'Standard', 500, '2018-05-21 13:15:22.451', NULL, NULL, 'testing', '64;322;399', 20);

-- main dish, chicken broth
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 500, 320, 1, 500, '406');
-- main dish, yummy
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 501, 320, 2, 500, '321');
-- main dish, carrots
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 502, 320, 3, 500, '81');
-- main dish, yummy
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 503, 320, 4, 500, '89');

INSERT INTO target (target, target_id, target_type, created, last_updated, last_used, target_name, target_tag_ids,
                    user_id)
VALUES ('TargetEntity', 501, 'Standard', '2018-05-21 13:15:22.451', NULL, NULL, 'testing', '64;322;399', 20);

INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 504, 320, 1, 501, '406');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 505, 320, 2, 501, '321');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 506, 320, 3, 501, '81');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 507, 320, 4, 501, '89');

INSERT INTO target (target, target_id, target_type, created, last_updated, last_used, target_name, target_tag_ids,
                    user_id)
VALUES ('TargetEntity', 502, 'Standard', '2018-05-21 13:15:22.451', NULL, NULL, 'testing', '64;322;399', 500);

INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 508, 320, 1, 502, '406;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 509, 320, 1, 502, '329;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 510, 320, 1, 502, '81;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 511, 320, 1, 502, '89;301');

INSERT INTO target (target, target_id, target_type, created, expires, last_updated, last_used, target_name,
                    target_tag_ids, user_id)
VALUES ('TargetEntity', 503, 'Standard', '2018-05-21 13:15:22.451', '2018-05-21 14:15:22.451', NULL, NULL, 'testing',
        '64;322;399', 500);

INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 512, 320, 1, 503, '406;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 513, 320, 1, 503, '329;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 514, 320, 1, 503, '81;301');
INSERT INTO target_slot (target, target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids)
VALUES ('TargetSlotEntity', 515, 320, 1, 503, '89;301');



-- proposals

--  proposal 500 for user, and target 500
insert into proposal (created, is_refreshable, user_id, proposal_id)
values (current_timestamp, false, 20, 500);
insert into proposal_context (current_approach_index, current_approach_type, meal_plan_id, proposal_id, target_id,
                              proposal_context_id)
values (0, 'WHEEL_MIXED', null, 500, 500, 500);
insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 500, 320, 1, 500);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (96, '89', 500, 500);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (37, '89', 500, 501);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (5, '64', 500, 502);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (42, '64', 500, 503);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (39, '322', 500, 504);

insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 66, 500, 320, 2, 501);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (106, '322;64;321', 501, 505);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (16, '322;399;321', 501, 507);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (103, '322;64;321', 501, 508);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (76, '322;321', 501, 509);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 500, 320, 3, 502);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (65, '322;399;406', 502, 510);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (105, '322;64;406', 502, 511);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (104, '322;406', 502, 512);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (64, '406', 502, 513);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (52, '406', 502, 514);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 500, 320, 4, 503);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (115, '322;81', 503, 515);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (61, '81', 503, 516);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (63, '81', 503, 517);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (10, '81', 503, 518);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (77, '81', 503, 519);



insert into proposal_approach (approach_number, instructions, proposal_context_id, proposal_approach_id)
values (0, '4;1;2;3', 500, 500);
update proposal_context
set current_approach_index=0,
    current_approach_type='WHEEL_MIXED',
    proposal_hash_code='0',
    target_hash_code='-623001283',
    target_id=500
where proposal_context_id = 500;


-- proposal 501 for user, and target 501
insert into proposal (created, is_refreshable, user_id, proposal_id)
values (current_timestamp, false, 20, 501);
insert into proposal_context (current_approach_index, current_approach_type, meal_plan_id, proposal_id, target_id,
                              proposal_context_id)
values (0, 'WHEEL_MIXED', null, 501, 501, 501);
insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 500, 320, 1, 510);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (96, '89', 510, 530);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (37, '89', 510, 531);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (5, '64', 510, 532);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (42, '64', 510, 533);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (39, '322', 510, 534);

insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 66, 501, 320, 2, 511);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (106, '322;64;321', 511, 535);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (16, '322;399;321', 511, 537);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (103, '322;64;321', 511, 538);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (76, '322;321', 511, 539);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 501, 320, 3, 512);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (65, '322;399;406', 512, 540);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (105, '322;64;406', 512, 541);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (104, '322;406', 512, 542);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (64, '406', 512, 543);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (52, '406', 512, 544);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, null, 501, 320, 4, 513);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (115, '322;81', 513, 545);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (61, '81', 513, 546);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (63, '81', 513, 547);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (10, '81', 513, 548);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (77, '81', 513, 549);



insert into proposal_approach (approach_number, instructions, proposal_context_id, proposal_approach_id)
values (0, '4;1;3', 501, 510);
insert into proposal_approach (approach_number, instructions, proposal_context_id, proposal_approach_id)
values (1, '1;3;4', 501, 511);
update proposal_context
set current_approach_index=0,
    current_approach_type='WHEEL_MIXED',
    proposal_hash_code='0',
    target_hash_code='-623001283',
    target_id=501
where proposal_context_id = 501;


-- proposal 502 for user, and target 501
insert into proposal (created, is_refreshable, user_id, proposal_id)
values (current_timestamp, false, 20, 502);
insert into proposal_context (current_approach_index, current_approach_type, meal_plan_id, proposal_id, target_id,
                              proposal_context_id)
values (0, 'WHEEL_MIXED', null, 502, 501, 502);
insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 96, 502, 320, 1, 620);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (37, '89', 510, 631);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (5, '64', 510, 632);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (42, '64', 510, 633);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (39, '322', 510, 634);

insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 66, 502, 320, 2, 521);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (106, '322;64;321', 511, 635);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (16, '322;399;321', 521, 637);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (103, '322;64;321', 521, 638);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (76, '322;321', 521, 639);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 105, 502, 320, 3, 522);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (65, '322;399;406', 522, 641);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (104, '322;406', 522, 642);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (64, '406', 522, 643);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (52, '406', 522, 644);



insert into proposal_slot (flat_matched_tag_ids, picked_dish_id, proposal_id, slot_dish_tag_id, slot_number, slot_id)
values (null, 115, 502, 320, 4, 523);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (61, '81', 523, 646);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (63, '81', 523, 647);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (10, '81', 523, 648);
insert into proposal_dish (dish_id, matched_tag_ids, slot_id, dish_slot_id)
values (77, '81', 523, 649);



insert into proposal_approach (approach_number, instructions, proposal_context_id, proposal_approach_id)
values (0, '4;1;3', 501, 510);
insert into proposal_approach (approach_number, instructions, proposal_context_id, proposal_approach_id)
values (1, '1;3;4', 501, 511);
update proposal_context
set current_approach_index=0,
    current_approach_type='WHEEL_MIXED',
    proposal_hash_code='0',
    target_hash_code='-623001283',
    target_id=501
where proposal_context_id = 501;

-- autotags
--    text
INSERT INTO public.auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,
                                         invert_filter)
VALUES ('TEXT', 1, 301, 'false', 'Soup', false);
INSERT INTO public.auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,
                                         invert_filter)
VALUES ('TEXT', 2, 323, 'false', 'Crock-pot;Crockpot;Crock pot', false);
INSERT INTO public.auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,
                                         invert_filter)
VALUES ('TAG', 3, 346, 'false', '9;434;375;88;372', null);
INSERT INTO public.auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,
                                         invert_filter)
VALUES ('TAG', 4, 199, 'true', '9;434;88;375;372', 433);



