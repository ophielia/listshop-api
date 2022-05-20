-- takes chicken stock out of meat
update tag_relation
set parent_tag_id = 379
where child_tag_id = 437;


delete
from shadow_tags;
update dish
set auto_tag_status = null;


with delete_layout_duplicates as (select t.tag_id,
                                         count(distinct ct.category_id),
                                         min(lc.category_id),
                                         max(lc.category_id) as cat_to_delete
                                  from tag t
                                           join category_tags ct on ct.tag_id = t.tag_id
                                           join list_category lc on ct.category_id = lc.category_id and lc.layout_id = 5
                                  group by t.tag_id
                                  having count(distinct ct.category_id) > 1
                                     and sum(case when lc.is_default = true then 1 else 0 end) = 1)
delete
from category_tags ct2 using delete_layout_duplicates d
where d.tag_id = ct2.tag_id
  and ct2.category_id = d.cat_to_delete;

-- delete orphaned list items
delete
from list_item t
where not exists(select from list tr where tr.list_id = t.list_id);

-- delete orphaned tags
delete
from category_tags
where tag_id in (select t.tag_id
                 from tag t
                 where not exists(select from tag_relation tr where tr.child_tag_id = t.tag_id)
                   and not exists(select from list_item li where li.tag_id = t.tag_id));
delete
from tag t
where not exists(select from tag_relation tr where tr.child_tag_id = t.tag_id)
  and not exists(select from list_item li where li.tag_id = t.tag_id);

-- duplicates?
with duplicates as (select lower(name), tag_type, is_group, count(*), array_agg(tag_id) as ids
                    from tag
                    where tag.user_id is null
                    group by 1, 2, 3
                    having count(*) > 1)
select t.tag_id,
       t.name,
       t.is_group,
       tr.parent_tag_id,
       count(li.item_id)     as list_use,
       count(ct.category_id) as cat_exists,
       count(dt.dish_id)     as used_in_dish
from tag t
         left outer join category_tags ct on t.tag_id = ct.tag_id
         left outer join tag_relation tr on t.tag_id = tr.child_tag_id
         left outer join list_item li on t.tag_id = li.tag_id
         left outer join dish_tags dt on t.tag_id = dt.tag_id
where t.tag_id in (select unnest(ids) from duplicates)
group by 1, 2, 3, 4
order by lower(name);

--update dish_tags set tag_id = 51033 where tag_id = 51031;
-- delete tags by id
begin;
delete
from category_tags
where tag_id in (50671, 51031, 50924, 50952, 51007, 50952, 50993);
delete
from tag_relation
where child_tag_id in (50671, 51031, 50924, 50952, 51007, 50952, 50993);

delete
from tag
where tag_id in (50671, 51031, 50924, 50952, 51007, 50952, 50993);
rollback;


-- assign tags for mom and dad
update tag
set user_id     = 34
  , is_verified = true
where tag_id in
      (50957, 50978, 50968, 50944, 50969, 50900, 51009, 50911, 50899, 51010, 50946, 50947, 50930, 50981, 50997, 50914,
       50998, 50847, 50972, 50949, 50863, 50833, 50986, 50935, 51000, 50950, 50973, 50752, 50812, 50674, 50807, 50709,
       50814, 50988, 50747, 50832, 50795, 50835, 50577, 50869, 50599, 51004, 50855, 50990, 50974, 50994, 50864, 51002,
       50808, 51013, 50836, 50878, 50905, 50744, 50870, 50925, 51008, 50912, 50843, 50934, 50964, 50929, 50982);

-- assign tags for me
update tag
set user_id     = 20,
    is_verified = true
where tag_id in (50865, 51018, 51012, 51014, 51017, 51016, 96, 275);

-- change name for "New" default groups to "Miscellaneous"
update
    tag
set name = 'Miscellaneous'
where tag_type_default = true
  and tag_type not in ('DishType', 'Rating');

-- clear powers from rating tag names
update tag t
set name = p.name
from tag_relation tr,
     tag p
where t.tag_id = tr.child_tag_id
  and p.tag_id = tr.parent_tag_id
  and t.tag_type = 'Rating';
