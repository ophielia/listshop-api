/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */

select *
from users;

delete
from dish_tags
where dish_id in (select dish_id
                  from dish d
                           join users u using (user_id)
                  where email = 'usertodelete4@test.com');
delete
from dish
where user_id = (select user_id from users where email = 'usertodelete4@test.com');
delete
from list_item
where list_id in (select list_id
                  from list d
                           join users u using (user_id)
                  where email = 'usertodelete4@test.com');
delete
from list_item
where list_id = (select user_id from users where email = 'usertodelete4@test.com');
delete
from meal_plan_slot
where meal_plan_id in (select meal_plan_id
                       from meal_plan m
                                join users u using (user_id)
                       where email = 'usertodelete4@test.com');
delete
from meal_plan
where user_id = (select user_id from users where email = 'usertodelete4@test.com');
delete
from users
where email = 'usertodelete4@test.com';



