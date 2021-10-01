-- rollback

delete
from dish_tags
where dish_id = 9999999;
delete
from dish
where dish_id = 9999999;