-- add column to dish_items
alter table dish_items
add column user_size boolean default false;

update dish_items i
set unit_size = 'medium'
from units u
where u.unit_id = i.unit_id
and u.type = 'UNIT';


