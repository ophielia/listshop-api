-- rollback
delete
from list_tag_stats
where tag_id = 999;
delete
from list_item
where tag_id = 999;
delete
from list
where list_id = 99999;
delete
from list
where list_id = 88888;
delete
from dish_tags
where tag_id = 999;
delete
from dish
where dish_id = 99999;
delete
from dish
where dish_id = 88888;
delete
from tag_relation
where child_tag_id = 999;
delete
from tag_relation
where parent_tag_id = 999;
delete
from category_tags
where tag_id = 999;
delete
from tag
where tag_id = 999;
delete
from users
where user_id in (99999, 88888);


