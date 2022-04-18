delete
from list_item
where list_id in (509990, 509991, 509999, 609990, 609991, 51000, 6666, 7777, 77777, 500777, 110000);
delete
from list
where list_id in (509990, 509991, 509999, 609990, 609991, 51000, 6666, 7777, 77777, 500777, 110000);


-- delete objects related to add menu plan to new or existing list
-- test add to list from meal plan
-- list 51000 with 3 items
delete
from list_item
where item_id in (510001, 510002, 510003, 70773, 609990, 609991, 609994);
delete
from list
where list_id = 51000;
-- meal plan with 2 dishes
-- contained in meal plan are tags 502, 503, and 510
-- dishes
delete
from meal_plan_slot
where meal_plan_id = 65505;
delete
from dish_tags
where dish_id in (66500, 66501);
delete
from meal_plan
where meal_plan_id = 65505;
delete
from dish
where dish_id in (66500, 66501);


-- delete new user and single list
delete from tag_relation where parent_tag_id = 999;
delete from tag_relation where child_tag_id = 999;
delete from category_tags where tag_id = 999;
delete from list_item where tag_id = 999;
delete from tag where tag_id = 999;
delete from list_item where list_id = 99999;
delete from list where list_id = 99999;
delete from users where user_id in (99999);