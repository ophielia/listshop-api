-- reset autotags
-- takes chicken stock out of meat
update tag_relation
set parent_tag_id = 379
where child_tag_id = 437


delete
from shadow_tags;
update dish
set auto_tag_status = null;

-- delete duplicates in category tags
with delete_layout_duplicates as (select t.tag_id,
                                         count(distinct ct.category_id),
                                         min(lc.category_id),
                                         max(lc.category_id) as cat_to_delete
                                  from tag t
                                           join category_tags ct on ct.tag_id = t.tag_id
                                           join list_category lc on ct.category_id = lc.category_id and lc.layout_id = 5
                                  group by t.tag_id
                                  having count(distinct ct.category_id) > 1
                                     and sum(case when lc.is_default = true then 1 else 0 end) = 0)
delete
from category_tags ct2 using delete_layout_duplicates d
where d.tag_id = ct2.tag_id
  and ct2.category_id = d.cat_to_delete;

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
