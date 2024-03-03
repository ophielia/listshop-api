-- now the data
insert into dish_items (dish_item_id, dish_id, tag_id)
select nextval('dish_item_sequence'), dish_id, tag_id
from dish_tags;
