/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

-- rollback
delete from dish_items where dish_id in ( 9999992, 9999993 );

delete
from dish_items
where dish_id in
      (select dish_id from dish where created_on >= now() -
    interval '1 second'
  and dish_name ilike 'test%');

delete
from dish
where created_on >= now() - interval '1 second'
  and dish_name ilike 'test%';

delete
from dish_items
where dish_id in
      (select dish_id from dish where created_on >= now() -
    interval '1 second'
  and dish_name ilike 'test%');
delete
from dish
where created_on >= now() - interval '1 second'
  and dish_name ilike 'test%';

delete
from dish
where dish_id in ( 9999992, 9999993 );

delete from user_devices where user_id = 20;


