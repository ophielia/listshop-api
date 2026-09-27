-- insert dish
insert into public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference)
values (9999992, null, 'Test Rating Dish', 20, '2019-10-05 07:23:55.673000 +00:00', 105,
        '2019-07-09 12:53:14.773362 +00:00', null);
insert into public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference)
values (9999993, null, 'Dish With Amounts', 20, '2019-10-05 07:23:55.673000 +00:00', 105,
        '2019-07-09 12:53:14.773362 +00:00', null);


-- insert tags - no amounts
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
-- ground beef
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 435);
-- green bell pepper
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 187);
-- canned dices tomatoes
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 110);
-- worcestershire sauce
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 457);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 123);
-- chili powder
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 184);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 360);
-- cheddar cheese
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 18);
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999992, 452);


-- dish items - amounts

-- tags
-- crockpot 323
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 323);
/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

-- vegetarian 199
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 199);
-- main dish 320
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 320);

-- ratings
-- rating yummy
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 325);
-- rating elegance
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 395);
-- rating quick to prepare
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 219);
-- rating ease of preparation
insert into dish_items (dish_item_id, dish_id, tag_id)
values (nextval('dish_item_sequence'), 9999993, 399);


-- ingredients
-- cheddar cheese
insert into dish_items (dish_item_id, dish_id, tag_id,
                        unit_id,quantity, whole_quantity, fractional_quantity,
                        raw_entry,raw_modifiers )
values (nextval('dish_item_sequence'), 9999993, 18,
        1008, 1.5, 1, 'OneHalf', '1 1/2 pound', 'grated'),
-- worcestershire sauce
(nextval('dish_item_sequence'), 9999993, 457,
 1001, 3.0, 3.0, null, '3 tablespoons', null),
-- chili powder
(nextval('dish_item_sequence'), 9999993, 184,
 1002, 0.25, null, 'OneEighth', '1/8 teaspoon', null),
-- canned dices tomatoes
(nextval('dish_item_sequence'), 9999993, 110,
 1029, 2, 2, null, '2 cans', null),
-- green bell pepper
 (nextval('dish_item_sequence'), 9999993, 187,
  1011, 4, 4.0, null, '4 ripe extra large', 'extra large|ripe'),
-- ground beef
(nextval('dish_item_sequence'), 9999993, 435,
 1008, 3.5, 3.0, 'OneHalf', '3.5 pounds', null);


-- insert user devices for authentication
insert into user_devices (user_device_id, user_id, name, model, os, os_version, client_type, build_number, client_device_id, client_version, token, last_login)
values (99920, 20, 'test', 'test', 'test', 'test', 'test', 'test', 'test', 'test', 'token20user', now());


update tag t
set power = 5
from tag_relation r
where r.child_tag_id = t.tag_id
  and tag_type = 'Rating'
  and r.parent_tag_id is null;



