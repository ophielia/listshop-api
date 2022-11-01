-- basic list (5000), 4 items different status - 5000, tags 501-504, items, 50001-50002
-- tag 6666 - replaced by tag 55
-- replacement list (5001), 1 item with tag_id 6666, 1 item without - tag_id 502
-- replacement double jeopardy (5002) - 1 item with tag_id 6666, 1 item with tag_id 55

insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 1, 'General', 500, 5000, 'ShoppingList1');

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (5000, 501, 50001, now() - interval '4 day', null, null, null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (5000, 502, 50002, now() - interval '4 day', null, null, now() - interval '3 day', null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (5000, 503, 50003, now() - interval '4 day', now() - interval '2 day', null, now() - interval '3 day', null, 1,
        null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (5000, 504, 50004, now() - interval '4 day', now() - interval '2 day', now() - interval '1 day',
        now() - interval '3 day', null, 1, null, null);



insert into tag (replacement_tag_id, description, is_verified, name, power, search_select, tag_type, tag_type_default,
                 tag_id, to_delete)
values (55, true, null, 'notdisplayed', 0, true, 'Ingredient', false, 6666, true);


insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 1, 'General', 500, 50001, 'ShoppingList2');

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (50001, 6666, 50005, now() - interval '4 day', null, null, null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count,
                      dish_sources, list_sources)
VALUES (50001, 502, 50006, now() - interval '4 day', null, null, now() - interval '3 day', null, 1, null, null);



insert into list (created_on, list_layout_id, list_types, user_id, list_id, name)
values (now(), 1, 'General', 500, 50002, 'ShoppingList3');

INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
 VALUES (50002, 6666, 50007, now() - interval '4 day', null , null, null, null, 1, null, null);
INSERT INTO list_item(
list_id, tag_id, item_id, added_on, crossed_off, removed_on, updated_on, free_text, used_count, dish_sources, list_sources)
VALUES (50002, 55, 50008, now() - interval '4 day', null , null, now() - interval '3 day', null, 1, null, null);
