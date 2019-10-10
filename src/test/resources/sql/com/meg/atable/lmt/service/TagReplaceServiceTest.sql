-- make new tag (will be replaced by rice - 13)
INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (999, NULL, 'tag_to_be_deleted', 'Ingredient', NULL, true, false, NULL, NULL);
INSERT INTO tag_relation (tag_relation_id, child_tag_id, parent_tag_id)
VALUES (99999, 999, 381);
INSERT INTO category_tags (category_id, tag_id)
VALUES (11, 999);


-- make new user with dishes and meal plans
INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (99999, 'email@email.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'email@email.com');
--dish
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status)
VALUES (99999, NULL, 'Test Dish with tag for delete', 99999, NULL, 105);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (99999, 999);
--list
insert into list (created_on, list_layout_id, list_types, user_id, list_id)
values (now(), 1, 'BaseList', 99999, 99999);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (99999, 999, 99999, now(), null, null, 1, null, null);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id)
VALUES (99999, 1, 1, 999, 99999);


INSERT INTO users (user_id, email, enabled, last_password_reset_date, password, username)
VALUES (88888, 'email2@email.com', true, NULL, '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',
        'email2@email.com');
INSERT INTO dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status)
VALUES (88888, NULL, 'Test Dish with tag for delete', 88888, NULL, 105);
INSERT INTO dish_tags (dish_id, tag_id)
VALUES (88888, 999);
--list
insert into list (created_on, list_layout_id, list_types, user_id, list_id)
values (now(), 1, 'BaseList', 88888, 88888);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (88888, 999, 88888, now(), null, null, 1, null, null);
INSERT INTO list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id)
VALUES (88888, 1, 1, 999, 88888);
