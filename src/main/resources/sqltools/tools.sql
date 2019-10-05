with upd as (select t.tag_id, p.name from tag t join tag_relation r on r.parent_tag_id = t.tag_id
join tag p on r.child_tag_id = p.tag_id
where t.tag_type = 'RATING')
update tag  p
set rating_family = upd.name
from upd
where p.tag_id = upd.tag_id



create view selectabletags as
select c.* from tag_relation c left outer join tag_relation p  on c.child_tag_id = p.parent_tag_id where p is null;

select ct.* from selectabletags t, tag ct where ct.tag_id = t.child_tag_id


# get all slots for list of dishes
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


-- all parent slots
﻿select distinct t.*
from tag t
join tag_relation r on t.tag_id = r.parent_tag_id

﻿select t.tag_id, t.name, t.auto_tag_flag,c.tag_id, c.name, c.auto_tag_flag
from tag t
join tag_relation r on t.tag_id = r.parent_tag_id
join tag c on r.child_tag_id = c.tag_id
where t.auto_tag_flag is not null
and (c.is_parent_tag is null or c.is_parent_tag = false);

-- update child slots with auto tag flag
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

-- adding a new auto_tag_instruction
﻿INSERT INTO public.auto_tag_instructions(
	instruction_type, instruction_id, assign_tag_id, is_invert, search_terms)
	VALUES ('TAG', nextval('auto_tag_instructions_sequence'), 346, false, '9,88,368, 372, 374, 375');

INSERT INTO public.auto_tag_instructions(
	instruction_type, instruction_id, assign_tag_id, is_invert, search_terms,invert_filter)
	VALUES ('TAG', nextval('auto_tag_instructions_sequence'), 199, true, '9,88,368, 372, 374, 375','320');

﻿INSERT INTO public.auto_tag_instructions(
	instruction_type, instruction_id, assign_tag_id, is_invert, search_terms)
	VALUES ('TEXT', nextval('auto_tag_instructions_sequence'), 323, false, 'crockpot,crock-pot,slow cooker,slow-cooker,slowcooker');



﻿-- clear proposals
delete  from target_proposal_dish;
delete  from target_proposal_slot;
update target set proposal_id = null;
delete  from target_proposal;

﻿-- clear targets
delete  from target_tags;
delete  from target_slots;
delete  from target_slot;
delete  from target;

﻿-- clear meal_plans
delete  from meal_plan_slot;
delete from meal_plan;

-- check tag search groups
﻿select tag_search_group_id,p.tag_id, p.name, m.tag_id, m.name
from tag_search_group g
join tag p on p.tag_id = g.group_id
join tag m on m.tag_id = g.member_id
order by p.name, m.name;

﻿-- clear proposals
delete  from proposal_context_slot;
delete  from proposal_context;

﻿-- clear lists
delete  from list_item;
delete  from list;

﻿-- clear autotags
delete  from auto_tag_instructions;
delete  from shadow_tags;


-- move tag function

﻿select movetag(100000);



--delete from tag_search_group where tag_search_group_id in (350, 302);

-- dump command
pg_dump -U bank -d atable -O --column-inserts > test.sql

-- dump command
pg_dump -U bank -d atable -O --column-inserts > test.sql


----- make sure all parent tags are not selectable
update tag set assign_select = false
where tag_id in (
select distinct t.tag_id from tag t
join tag_relation tr on t.tag_id = tr.parent_tag_id
and assign_select = true);

-- working on copy tags => fine grained
-- make sure assign select is good for everything
update tag set assign_select = false where tag_id in (
select distinct tag_id from tag t
join tag_relation r on t.tag_id = r.parent_tag_id
left join tag_relation cr on r.parent_tag_id = cr.child_tag_id
and cr.child_tag_id is null
where t.assign_select = true);

-- select for new categories
--base
select distinct pr.*
from tag ch
join tag_relation tr on tr.child_tag_id = ch.tag_id
join tag pr on pr.tag_id = tr.parent_tag_id
where ch.assign_select = true
and ch.tag_type in ('Ingredient','NonEdible')
-- prep for insert
select distinct pr.name as name, 99 as category_id, 1 as layout_id , 1 as display_order
from tag ch
join tag_relation tr on tr.child_tag_id = ch.tag_id
join tag pr on pr.tag_id = tr.parent_tag_id
where ch.assign_select = true
and ch.tag_type in ('Ingredient','NonEdible')

insert into list_category (name, category_id, layout_id, display_order)
select distinct pr.name as name, nextval('list_layout_category_sequence') as category_id, 1 as layout_id , 1 as display_order
from tag ch
join tag_relation tr on tr.child_tag_id = ch.tag_id
join tag pr on pr.tag_id = tr.parent_tag_id
where ch.assign_select = true
and ch.tag_type in ('Ingredient','NonEdible')

-- final (but in bad order)
insert into list_category (name, category_id, layout_id, display_order)
select name as name, nextval('list_layout_category_sequence') as category_id, 1 as layout_id , 1 as display_order
from george;

create temporary table george as
select distinct pr.name as name
from tag ch
join tag_relation tr on tr.child_tag_id = ch.tag_id
join tag pr on pr.tag_id = tr.parent_tag_id
where ch.assign_select = true
and ch.tag_type in ('Ingredient','NonEdible')

insert into category_tags (category_id, tag_id)
select lc.category_id as category_id,tr.child_tag_id as tag_id
from list_category lc
join tag t on t.name = lc.name
join tag_relation tr on t.tag_id = tr.parent_tag_id
and t.assign_select = false
where lc.layout_id = 1;

select * from category_tags ct
join list_category lc using (category_id)
--select * from list_category
where layout_id = 1


-- weird layout category bug
﻿select ct.*,lc.category_id, lc.layout_id,lc.name,t.tag_id,t.name from tag t
join category_tags ct using (tag_id)
join list_category lc using (category_id)
where layout_id =5
and (lc.name = 'Meat' or lc.name = 'Other')
order by t.name;

delete
from category_tags
where category_id = 5
and tag_id in  (select tag_id from category_tags where category_id = 10  )



nextval('list_layout_category_sequence')


mvn flyway:info
mvn flyway:baseline -Dflyway.baselineVersion=5


INSERT INTO tag_search_group (tag_search_group_id, group_id, member_id)
 VALUES (nextval('tag_search_group_sequence'), 368, 50591);


-- checking autotags --


with find_tags as (
  select regexp_split_to_table(search_terms, E'\;') as to_find
  from auto_tag_instructions
  where instruction_id = 3)
select *
from find_tags f
       join tag t on t.tag_id = cast(f.to_find as bigint);

select regexp_split_to_table(search_terms, E'\;'), a.*
from auto_tag_instructions a;


1. backup dishdev
2. copy dishsnap to dishdev
3. move migrate scripts to dev
4. mvn flyway:migrate
5. fire up
6. merge merge_tags => develop
7. merge develop => master
8. checkout master
9. mvn flyway:migrate dishsnap






