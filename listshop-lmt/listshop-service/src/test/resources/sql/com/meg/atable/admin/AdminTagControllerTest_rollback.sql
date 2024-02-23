--- new user, and single list
delete
from users
where user_id in (101010);
delete
from tag_relation
where child_tag_id in (999999, 888888);
delete
from tag
where tag_id in (999999, 888888);


delete from food_category_mapping where category_id in (3,13,18);
delete from tag_relation where child_tag_id  in (888999, 8881009, 8881019, 9991019,9991029,9991039);
delete from food_categories where category_id in (3,13,18);
delete from foods where food_id in (9000, 9001, 9002, 9003, 9004, 9005);
delete from tag where tag_id in (888999, 8881009, 8881019, 9991019,9991029,9991039);
