-- rollback

delete
from dish_tags
where dish_id = 9999999;
delete
from dish
where dish_id = 9999999;


delete
from dish_tags
where dish_id in
      (select dish_id from dish where created_on >= now() -
    interval '1 second'
  and dish_name ilike 'test%');
delete
from dish
where created_on >= now() - interval '1 second'
  and dish_name ilike 'test%';