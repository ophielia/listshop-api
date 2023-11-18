-- insert dish
insert into public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference)
values (9999999, null, 'Test Rating Dish', 20, '2019-10-05 07:23:55.673000 +00:00', 105,
        '2019-07-09 12:53:14.773362 +00:00', null);

-- insert tags
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 363);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 419);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 325);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 425);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 315);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 395);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 219);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 399);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 435);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 187);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 110);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 457);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 123);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 184);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 360);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 18);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999999, 452);





