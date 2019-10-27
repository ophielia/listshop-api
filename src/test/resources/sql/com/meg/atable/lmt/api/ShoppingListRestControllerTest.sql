-- lists - ids 500-503
-- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 1, 'General', 20, 509990, 'added to');
-- active list - id 501
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 11, 'General', 20, 509991, 'added from');


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
