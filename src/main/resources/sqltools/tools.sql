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
AS $BODY$  DECLARE
      pOrigUser ALIAS for $1;
      pNewUser ALIAS for $2;
      pDish	record;
      nDish int;
   BEGIN
      FOR pDish IN select * from Dish o
      				left join dish n on o.dish_name = n.dish_name and n.user_id = pNewUser
					where o.user_id = pOrigUser
					and n.dish_id is null LOOP
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


-- all parent tags
﻿select distinct t.*
from tag t
join tag_relation r on t.tag_id = r.parent_tag_id

﻿select t.tag_id, t.name, t.auto_tag_flag,c.tag_id, c.name, c.auto_tag_flag
from tag t
join tag_relation r on t.tag_id = r.parent_tag_id
join tag c on r.child_tag_id = c.tag_id
where t.auto_tag_flag is not null
and (c.is_parent_tag is null or c.is_parent_tag = false);

-- update child tags with auto tag flag
﻿update tag as c
set auto_tag_flag = t.auto_tag_flag
from tag as t, tag_relation as r
where t.tag_id = r.parent_tag_id
and  r.child_tag_id = c.tag_id
and t.auto_tag_flag is not null
and (c.is_parent_tag is null or c.is_parent_tag = false)

-- skeleton for merge tag function
﻿-- replace all occurrences of old with new in dish_tags
-- replace all occurrences of old with new in list_item
-- replace all occurrences of old with new in category_tags
-- delete old from tag_relation
-- delete old from tag