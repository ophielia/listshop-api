with upd as (select t.tag_id, p.name from tag t join tag_relation r on r.parent_tag_id = t.tag_id
join tag p on r.child_tag_id = p.tag_id
where t.tag_type = 'Rating')
update tag  p
set rating_family = upd.name
from upd
where p.tag_id = upd.tag_id



create view selectabletags as
select c.* from tag_relation c left outer join tag_relation p  on c.child_tag_id = p.parent_tag_id where p is null;

select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id


# get all tags for list of dishes
﻿select *
from dish d
inner join dish_tags td using(dish_id)
inner join tag t using(tag_id)
where dish_id in (75, 110, 211, 200, 181, 222, 223, 224, 243)

﻿select t.name, t.tag_type,count(*)
from dish d
inner join dish_tags td using(dish_id)
inner join tag t using(tag_id)
where dish_id in (75, 110, 211, 200, 181, 222, 223, 224, 243)
and t.tag_type = 'Ingredient'
group by t.name, t.tag_type

﻿select t.name, count(*)
from dish d
inner join dish_tags td using(dish_id)
inner join tag t using(tag_id)
where dish_id in (75, 110, 211, 200, 181, 222, 223, 224, 243)
and t.tag_type = 'Ingredient'
group by t.name


--- function for copying dishes from one user to another
﻿CREATE OR REPLACE FUNCTION public.copy_dishes(integer,integer)
    RETURNS integer
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$   DECLARE
      pOrigUser ALIAS for $1;
      pNewUser ALIAS for $2;
      pDish	record;
      nDish int;
   BEGIN
      FOR pDish IN select * from Dish where user_id = pOrigUser LOOP
         insert into dish (dish_id,description, dish_name, user_id, last_added)
				select nextval('hibernate_sequence'),description, dish_name, pNewUser,last_added
				from dish where user_id = pOrigUser and dish_id = pDish.dish_id
         returning dish_id into nDish;
         RAISE NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
         insert into dish_tags (dish_id, tag_id)
			select nDish, tag_id from dish_tags where dish_id = pDish.dish_id;
      END LOOP;
      return 1;
   END;$BODY$