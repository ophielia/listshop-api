/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

insert into list (list_id,created_on, list_layout_id, list_types, user_id,  name)
values
    (33333,now(), 5, 'BaseList', 500,  'Exiting'),
    -- list, items,  no existing details
    (88888,now(), 5, 'BaseList', 500,  'No Existing Details'),
    -- list, items,  existing details
    (77777,now(), 5, 'BaseList', 500,  'Existing Details'),
    -- list, items,  existing details, multiples
    (66666,now(), 5, 'BaseList', 500,  'Multiple Existing Details'),
    -- list, items,  existing detail, mixed amounts
    (55555,now(), 5, 'BaseList', 500,  'Existing Detail, Mixed Amounts')
;

--    carrot (81)
--    butter - (348)
INSERT INTO list_item(
    list_id, item_id, tag_id,  added_on, crossed_off, removed_on, updated_on,  used_count, dish_sources, list_sources)
VALUES
    (88888, 1888888, 81, now() - interval '4 day', null , null, null,  1, null, null),
    (88888, 2888888, 348, now() - interval '4 day', null , null, null,  1, null, null),
    (77777, 1777778, 81, now() - interval '4 day', null , null, null,  1, null, null),
    (77777, 2777778, 348, now() - interval '4 day', null , null, null,  1, null, null),
    (66666, 2666668, 348, now() - interval '4 day', null , null, null,  1, null, null),
    (55555, 2555558, 348, now() - interval '4 day', null , null, null,  1, null, null);


INSERT INTO list_item_details (item_id,item_detail_id,  used_count, linked_list_id, linked_dish_id, quantity,  whole_quantity,fractional_quantity,unit_id, raw_entry)
VALUES
    (1888888,18888881, 1, 33333, null, null, null, null,null, null),
    (2888888,28888881, 1, 33333, 53, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (1777778,17777781, 1, 33333, null, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (1777778,17777782, 1, 77777, null, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2777778,27777781, 1, 33333, 53 , 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2777778,27777782, 1, 77777, 53, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2666668,26666681, 1, 33333, 53 , 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2666668,26666682, 1, 66666, 53, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2666668,26666683, 1, 33333, null , 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2666668,26666684, 1, 66666, null, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2555558,25555581, 1, 55555, null, 1.5,1,'OneHalf', 1000, '1.5 cups'),
    (2555558,25555582, 1, 33333, null, null,null,null, null, '1.5 cups');


