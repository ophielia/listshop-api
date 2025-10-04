--- new user, and single list
delete
from users
where user_id in (99999);
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (99999, 'username@testitytest.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'email@email.com');
insert into list (created_on, list_layout_id, name, user_id, list_id)
values (now(), 1, 'BaseList', 99999, 99999);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power)
VALUES (999, NULL, 'tag_to_be_deleted', 'Ingredient', NULL, NULL, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (99999, 999, 381);
INSERT INTO category_tags (category_id, tag_id)
VALUES (5, 999);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (99999, 999, 99999, now(), null, null, 1, null, null);

delete
from users
where user_id = 504;
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (504, 'nostarterlist@testitytest.com', true, NULL,
        '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'email@email.com');

-- lists - ids 500-503
-- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 20, 509990, 'added to', false);
-- starter list - id 509991
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 509991, 'added from', true);
-- user 504 doesn't have a starter list
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 504, 509999, 'added from', false);
-- user 504 doesn't have a starter list
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 500777, 'merge test', false);
-- copy from / to: sourceList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', null, 'General', 20, 7777, 'list operations - source', false);
-- copy from / to: destinationList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', null, 'General', 20, 6666, 'list operations - source', false);
-- copy from / to: sourceList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 77777, 'list operations - source', false);

-- test list for removing from lists
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 500, 609990, 'added to', false);
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 500, 609991, 'remove from this list', false);


-- list items - list 509990 - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 501, 509990, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 502, 509991, now(), null, null, 1, null, '509991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 503, 509992, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 500, 509993, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 504, 509994, now(), null, null, 1, null, '509991');

-- list items - list 509991 - starter list
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509991, 295, 50999321, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509991, 296, 50999322, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509991, 307, 50999323, now(), null, null, 1, null, null);

-- list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 501, 509999, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 502, 509981, now(), null, null, 1, null, '509991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 503, 509982, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 500, 509983, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 504, 509984, now(), null, null, 1, null, '509991');

-- list items for list from which to delete a list
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 501, 609990, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 502, 609991, now(), null, null, 1, null, '609991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 503, 609992, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 500, 609993, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 504, 609994, now(), null, null, 1, null, '609991');

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609991, 501, 609980, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609991, 504, 609984, now(), null, null, 1, null, null);


-- merge list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 501, 500770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 502, 500771, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 503, 500772, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 500, 500773, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 504, 500774, '2019-07-12', null, null, 1, null, null);

-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 500, 70773, '2019-07-12', '2019-07-12', null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 501, 70770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 502, 70771, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 505, 707711, '2019-07-12', '2019-07-12', null, 1, null, null);

insert into list_item_details
(item_detail_id, item_id, linked_list_id, used_count)
values (7077110, 70770, 7777, 1),
       (7077111, 70771, 7777, 1),
       (707711112, 707711, 7777, 1),
       (7077112, 70772, 7777, 1),
       (7077113, 70773, 7777, 1);


-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 500, 770773, '2019-07-12', '2019-07-12', null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 501, 770770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 502, 770771, '2019-07-12', '2019-07-12', null, 1, null, null);


-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 501, 60660, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 502, 60661, '2019-07-12', null, null, 1, null, null);

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 503, 60662, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 505, 60663, '2019-07-12', null, null, 1, null, null);

insert into list_item_details
    (item_detail_id, item_id, linked_list_id, used_count)
values (6066110, 60660, 6666, 1),
       (6066111, 60661, 6666, 1),
       (6066112, 60662, 6666, 1),
       (6066113, 60663, 6666, 1);
-- test with 501, 503, 504

-- test add to list from meal plan
-- list 51000 with 3 items
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 500, 51000, 'added from', false);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 81, 510001, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 1, 510002, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 12, 510003, now(), null, null, 1, null, null);


insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (51000111, 510001, null, 1);
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (51000112, 510002, null, 1);
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (51000113, 510003, null, 1);

-- meal plan with 2 dishes
-- contained in meal plan are tags 502, 503, and 510
-- dishes
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish1', null, 500, 66500);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish1', null, 500, 66501);
--dish 1 - tags 502 and 510
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 66500, 1);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 66500, 436);
-- dish 2 - tags 502 and 503
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 66501, 1);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 66501, 12);

insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 5', 500, 65505);

-- slots for meal plan -
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (66501, 65505, 65500);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (66500, 65505, 65501);


-- new test for merge list - list 10000
insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (110000, '2022-04-16 05:32:38.898000 +00:00', 20, null, 5, '2022-04-16 07:32:39.660000', null, false,
        'Shopping List');

insert into list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count,
                       category_id, dish_sources, list_sources, removed_on, updated_on)
values (110000, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 256, 1, null, '109', null, null,
        null),
       (110014, '2022-04-16 03:32:39.605000 +00:00', null, null, null, 110000, null, 32, 1, null, '112', now(), now(),
        null),
       (110015, '2022-04-16 03:32:39.606000 +00:00', null, null, null, 110000, null, 33, 1, null, '112', null, null,
        null),
       (110016, '2022-04-16 03:32:39.606000 +00:00', null, null, null, 110000, null, 34, 1, null, '112', null, null,
        null),
       (110001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 41, 1, null, '109', null, null,
        null),
       (110002, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 460, 1, null, '109', null, null,
        null),
       (110003, '2022-04-16 03:32:39.319000 +00:00', null, null, null, 110000, null, 13, 1, null, '109', null, null,
        null),
       (110004, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 15, 1, null, '109', now(), now(),
        now()),
       (110005, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 16, 1, null, '109', null, null,
        null),
       (110006, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 81, 2, null, '112;109', null, null,
        '2022-04-20 03:32:39.607000 +00:00'),
       (110007, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 19, 1, null, '109', null, null,
        null),
       (110008, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 212, 1, null, '109', null, null,
        null),
       (110009, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 21, 1, null, '109', null, null,
        null),
       (110010, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 437, 1, null, '109', null, null,
        null),
       (110011, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 217, 1, null, '109', null, null,
        null),
       (110012, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 443, 1, null, '109', null, null,
        null),
       (110013, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110000, null, 318, 1, null, '109', null, null,
        null);
-- details
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (110000111, 110000, 109, 1),
       (110014111, 110014, 112, 1),
       (110015111, 110015, 112, 1),
       (110016111, 110016, 112, 1),
       (110001111, 110001, 109, 1),
       (110002111, 110002, 109, 1),
       (110003111, 110003, 109, 1),
       (110004111, 110004, 109, 1),
       (110005111, 110005, 109, 1),
       (110006111, 110006, 112, 1),
       (110006111, 110006, 109, 1),
       (110007111, 110007, 109, 1),
       (110008111, 110008, 109, 1),
       (110009111, 110009, 109, 1),
       (110010111, 110010, 109, 1),
       (110011111, 110011, 109, 1),
       (110012111, 110012, 109, 1),
       (110013111, 110013, 109, 1);

-- test for skipping merge
-- new test for merge list - list 10000
insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (110099, '2022-04-16 05:32:38.898000 +00:00', 20, null, 5, '2022-04-16 07:32:39.660000', null, false,
        'Shopping List');

insert into list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count,
                       category_id, dish_sources, list_sources, removed_on, updated_on)
values (91110000, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 256, 1, null, '109', null, null,
        null),
       (91110014, '2022-04-16 03:32:39.605000 +00:00', null, null, null, 110099, null, 32, 1, null, '112', null, null,
        null),
       (91110015, '2022-04-16 03:32:39.606000 +00:00', null, null, null, 110099, null, 33, 1, null, '112', null, null,
        null),
       (91110016, '2022-04-16 03:32:39.606000 +00:00', null, null, null, 110099, null, 34, 1, null, '112', null, null,
        null),
       (91110001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 41, 1, null, '109', null, null,
        null),
       (91110002, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 460, 1, null, '109', null, null,
        null),
       (91110003, '2022-04-16 03:32:39.319000 +00:00', null, null, null, 110099, null, 13, 1, null, '109', null, null,
        null),
       (91110004, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 15, 1, null, '109', null, null,
        now()),
       (91110005, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 16, 1, null, '109', null, null,
        null),
       (91110006, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 81, 2, null, '112;109', null, null,
        '2022-04-20 03:32:39.607000 +00:00'),
       (91110007, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 19, 1, null, '109', null, null,
        null),
       (91110008, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 212, 1, null, '109', null, null,
        null),
       (91110009, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 21, 1, null, '109', null, null,
        null),
       (91110010, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 437, 1, null, '109', null, null,
        null),
       (91110011, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 217, 1, null, '109', null, null,
        null),
       (91110012, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 443, 1, null, '109', null, null,
        null),
       (91110013, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 110099, null, 318, 1, null, '109', null, null,
        null);
-- and the details
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (91110000111, 91110000, 109, 1),
       (91110014111, 91110014, 112, 1),
       (91110015111, 91110015, 112, 1),
       (91110016111, 91110016, 112, 1),
       (91110001111, 91110001, 109, 1),
       (91110002111, 91110002, 109, 1),
       (91110003111, 91110003, 109, 1),
       (91110004111, 91110004, 109, 1),
       (91110005111, 91110005, 109, 1),
       (91110006112, 91110006, 112, 1),
       (91110006111, 91110006, 109, 1),
       (91110007111, 91110007, 109, 1),
       (91110008111, 91110008, 109, 1),
       (91110009111, 91110009, 109, 1),
       (91110010111, 91110010, 109, 1),
       (91110011111, 91110011, 109, 1),
       (91110012111, 91110012, 109, 1),
       (91110013111, 91110013, 109, 1);

-- test for merge conflicts
-- insert two tags with user and standard name the same
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power, user_id)
VALUES (12001, NULL, 'tag conflict - delete', 'Ingredient', NULL, NULL, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power, user_id)
VALUES (13001, NULL, 'tag conflict - delete', 'Ingredient', NULL, NULL, NULL, 20);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power, user_id)
VALUES (12002, NULL, 'tag conflict - add', 'Ingredient', NULL, NULL, NULL, NULL);
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, is_verified,
                 power, user_id)
VALUES (13002, NULL, 'tag conflict - add', 'Ingredient', NULL, NULL, NULL, 20);

insert into category_tags (category_id, tag_id)
values (5, 12001),
       (5, 12002),
       (5, 13001),
       (5, 13002);
-- list contains one item (which will be removed during merge)
-- new test for merge list - list 10000
insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (120000, '2022-03-16 05:32:38.898000 +00:00', 20, null, 5, '2022-03-16 07:32:39.660000', null, false,
        'Shopping List - Conflicts');

insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (130000, '2022-03-16 05:32:38.898000 +00:00', 20, null, 5, '2022-03-16 07:32:39.660000', null, false,
        'Shopping List - Empty');

-- test stale client list merge

insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (11000001, '2022-04-16 05:32:38.898000 +00:00', 20, null, 5, now(), null, false,
        'Shopping List');

insert into list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count,
                       category_id, dish_sources, list_sources, removed_on, updated_on)
values (11000001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 256, 1, null, '109', null,
        null,
        null),
       (22110001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 41, 1, null, '109', null, null,
        null),
       (22110002, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 460, 1, null, '109', null,
        null,
        null),
       (22110003, '2022-04-16 03:32:39.319000 +00:00', null, null, null, 11000001, null, 13, 1, null, '109', null, null,
        null),
       (22110004, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 15, 1, null, '109', now(),
        now(),
        now()),
       (22110005, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 16, 1, null, '109', null, null,
        null),
       (22110006, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 81, 2, null, '112;109', null,
        null,
        '2022-04-20 03:32:39.607000 +00:00'),
       (22110007, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 19, 1, null, '109', null, null,
        null),
       (22110008, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 212, 1, null, '109', null,
        null,
        null),
       (22110009, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 21, 1, null, '109', null, null,
        null),
       (22110010, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 437, 1, null, '109', null,
        null,
        null),
       (22110011, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 217, 1, null, '109', null,
        null,
        null),
       (22110012, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 443, 1, null, '109', null,
        null,
        null),
       (22110013, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 11000001, null, 318, 1, null, '109', null,
        null,
        null);
-- and the details
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (11000001111, 11000001, 109, 1),
       (22110001111, 22110001, 109, 1),
       (22110002111, 22110002, 109, 1),
       (22110003111, 22110003, 109, 1),
       (22110004111, 22110004, 109, 1),
       (22110005111, 22110005, 109, 1),
       (22110006111, 22110006, 112, 1),
       (22110006112, 22110006, 109, 1),
       (22110007111, 22110007, 109, 1),
       (22110008111, 22110008, 109, 1),
       (22110009111, 22110009, 109, 1),
       (22110010111, 22110010, 109, 1),
       (22110011111, 22110011, 109, 1),
       (22110012111, 22110012, 109, 1),
       (22110013111, 22110013, 109, 1);

-- my user layout list

insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (10101010, '2022-04-16 05:32:38.898000 +00:00', 20, null, null, now(), null, false,
        'Shopping List');

insert into list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count,
                       category_id, dish_sources, list_sources, removed_on, updated_on)
values (10101010, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 10101010, null, 33, 1, null, '109', null,
        null,
        null),
       (2992110001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 10101010, null, 34, 1, null, '109', null,
        null,
        null),
       (2992110002, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 10101010, null, 32, 1, null, '109', null,
        null,
        null),
       (2992110003, '2022-04-16 03:32:39.319000 +00:00', null, null, null, 10101010, null, 44, 1, null, '109', null,
        null,
        null),
       (2992110004, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 10101010, null, 53, 1, null, '109', now(),
        now(),
        now())
;
-- details
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (10101010111, 10101010, 109, 1),
       (2992110001111, 2992110001, 109, 1),
       (2992110002111, 2992110002, 109, 1),
       (2992110003111, 2992110003, 109, 1),
       (2992110004111, 2992110004, 109, 1);

-- my user custom layout
update list_layout
set is_default = false
where user_id = 20;
insert into list_layout (layout_id, name, user_id, is_default)
values (nextval('list_layout_sequence'), 'Special', 20, true);

-- new category for default user layout
insert into list_category (category_id, name, layout_id, display_order, is_default)
select nextval('list_layout_category_sequence'), 'Special' as name, layout_id, 20, false
from list_layout
where user_id = 20
  and is_default = true;

insert into category_tags (category_id, tag_id)
select category_id, 33 -- tomatoes
from list_category lc
         join list_layout ll on lc.layout_id = ll.layout_id
where user_id = 20
  and ll.is_default = true
  and lc.name = 'Special';


-- other user layout list - uses standard

insert into list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list,
                  name)
values (90909090, '2022-04-16 05:32:38.898000 +00:00', 34, null, 5, now(), null, false,
        'Shopping List');

insert into list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count,
                       category_id, dish_sources, list_sources, removed_on, updated_on)
values (90909090, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 90909090, null, 33, 1, null, '109', null,
        null,
        null),
       (922110001, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 90909090, null, 34, 1, null, '109', null,
        null,
        null),
       (922110002, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 90909090, null, 32, 1, null, '109', null,
        null,
        null),
       (922110003, '2022-04-16 03:32:39.319000 +00:00', null, null, null, 90909090, null, 44, 1, null, '109', null,
        null,
        null),
       (922110004, '2022-04-16 03:32:39.320000 +00:00', null, null, null, 90909090, null, 53, 1, null, '109', now(),
        now(),
        now())
;
--details
insert into list_item_details
(item_detail_id, item_id, linked_dish_id, used_count)
values (90909090111, 90909090, 109, 1),
       (922110001111, 922110001, 109, 1),
       (922110002111, 922110002, 109, 1),
       (922110003111, 922110003, 109, 1),
       (922110004111, 922110004, 109, 1);
