update list
set last_update = now()
where name = 'Grocery Store';

-- adjust rating tags for Lemony Pasta
insert into public.dish_tags (dish_id, tag_id)
values (53, 19),
       (53, 24),
       (53, 104),
       (53, 109),
       (53, 117),
       (53, 131),
       (53, 193),
       (53, 303),
       (53, 320),
       (53, 334),
       (53, 344),
       (53, 348),
       (53, 354),
       (53, 360),
       (53, 406),
       (53, 218),
       (53, 400),
       (53, 346),
       (53, 328),
       (53, 395),
       (53, 417),
       (53, 426),
       (53, 321);

-- adding list "Corner Store"
insert into public.list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id,
                         is_starter_list, name)
values (50991, '2023-06-28 13:40:43.093000 +00:00', 34, null, null, '2023-06-28 15:40:57.794000', null, false,
        'Corner Store');