delete
from list_item
where list_id in
      (509990, 509991, 509999, 609990, 609991, 51000, 6666, 7777, 77777, 500777, 110000,110099, 11000001, 90909090, 10101010);
delete
from list
where list_id in
      (509990, 509991, 509999, 609990, 609991, 51000, 6666, 7777, 77777, 500777, 110000, 110099,11000001, 90909090, 10101010);


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

delete from dish_items where dish_id in (66500, 66501);

-- delete new user and single list
delete
from tag_relation
where parent_tag_id = 999;
delete
from tag_relation
where child_tag_id = 999;
delete
from category_tags
where tag_id = 999;
delete
from list_item
where tag_id = 999;
delete
from tag
where tag_id = 999;
delete
from list_item
where list_id = 99999;
delete
from list
where list_id = 99999;
delete
from users
where user_id in (99999);

-- delete data from merge conflict test
delete
from list_item
where list_id = 130000;
delete
from list
where list_id = 130000;
delete
from list_item
where list_id = 120000;
delete
from list
where list_id = 120000;
delete
from category_tags
where tag_id in (12001, 12002, 13001, 13002);
delete
from tag
where tag_id in (12001, 12002, 13001, 13002);


-- delete custom layout
delete
from category_tags ct using list_layout l, list_category lc
where l.layout_id = lc.layout_id
  and user_id = 20
  and l.name = 'Special'
  and ct.category_id = lc.category_id;
delete
from list_category lc using list_layout l
where l.layout_id = lc.layout_id and user_id = 20 and l.name = 'Special';
delete
from list_layout
where user_id = 20
  and name = 'Special';
update list_layout
set is_default = true
where user_id = 20;