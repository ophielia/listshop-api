INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (nextval('tag_sequence'), NULL, 'Dry Goods', 'Ingredient', NULL, false, false, NULL, NULL);

INSERT INTO tag (tag_id, description, name, tag_type, tag_type_default, assign_select, search_select, is_verified,
                 power)
VALUES (nextval('tag_sequence'), NULL, 'Cold Goods', 'Ingredient', NULL, false, false, NULL, NULL);


insert into tag_relation (tag_relation_id, child_tag_id)
select nextval('tag_relation_sequence') as tag_relation_id, tag_id as child_tag_id
from tag
where name = 'Dry Goods'
   or name = 'Cold Goods';

-- Dry
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Dry Goods'
    and c.tag_id in (409, 50504, 379, 392, 423, 386, 413, 376))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;

-- Bakery
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Bakery and Bread Products'
    and c.tag_id in (382))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;

-- Cold
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Cold Goods'
    and c.tag_id in (403, 378, 405, 50256))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;

-- Meat
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Meat'
    and c.tag_id in (373))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;

-- Condiments
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Condiments'
    and c.tag_id in (379))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;

-- Household
with tagrel_upd as (
  select p.tag_id as parent_id, c.*
  from tag p,
       tag c
  where p.name = 'Household Supplies'
    and c.tag_id in (367, 216, 383))
update tag_relation r
set parent_tag_id = u.parent_id
from tagrel_upd u
where r.child_tag_id = u.tag_id;




