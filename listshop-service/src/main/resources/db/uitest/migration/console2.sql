select dish_id, reference, dish_name
from dish
where user_id = 34
order by dish_id desc

select reference, user_id, *
from dish
where dish_name ilike '%cookies%'


select d.dish_name, d.reference
from meal_plan_slot s
         join dish d on d.dish_id = s.dish_dish_id
where meal_plan_id = 50550


select t.tag_id
from dish d
         join dish_tags dt on dt.dish_id = d.dish_id
         join tag t on t.tag_id = dt.tag_id
where d.user_id = 34
  and t.user_id is not null


select *
from dish
where dish_id = 56652

select *
from dish
where dish_id = 56652
  and user_id = 34


select *
from users
order by user_id desc


select *
from user_devices
where user_id = 42;