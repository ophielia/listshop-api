-- insert dish
insert into public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference)
values (9999992, null, 'Test Rating Dish', 20, '2019-10-05 07:23:55.673000 +00:00', 105,
        '2019-07-09 12:53:14.773362 +00:00', null);

-- insert tags
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 363);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 419);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 325);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 425);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 315);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 395);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 219);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 399);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 435);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 187);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 110);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 457);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 123);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 184);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 360);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 18);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 452);





