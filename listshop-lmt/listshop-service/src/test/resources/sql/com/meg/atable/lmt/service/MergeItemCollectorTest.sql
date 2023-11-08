insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 1, 'BaseList', 500, 5000, 'Hopping sit 2');

INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
 VALUES (5000, 501, 50001, now() - interval '4 day', null , null, null, null, 1, null, null);
INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
VALUES (5000, 502, 50002, now() - interval '4 day', null , null, now() - interval '3 day', null, 1, null, null);
INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
VALUES (5000, 503, 50003, now() - interval '4 day', now() - interval '2 day' , null, now() - interval '3 day', null, 1, null, null);
INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
VALUES (5000, 504, 50004, now() - interval '4 day', now() - interval '2 day' , now() - interval '1 day', now() - interval '3 day', null, 1, null, null);