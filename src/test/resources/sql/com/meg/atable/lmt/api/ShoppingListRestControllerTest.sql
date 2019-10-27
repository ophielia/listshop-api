-- lists - ids 500-503
-- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 20, 509990, 'added to', false);
-- active list - id 501
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 509991, 'added from', true);
-- user 504 doesn't have a starter list
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 504, 509999, 'added from', false);



-- list items - four items, for active list - id 501,502,503,500
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