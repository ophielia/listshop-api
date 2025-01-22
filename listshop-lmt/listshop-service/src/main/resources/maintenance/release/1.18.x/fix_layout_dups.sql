with dups as (select c.tag_id,
                     count(distinct coalesce(ll.user_id, 1)),
                     count(distinct l.category_id),
                     array_agg(l.category_id) as all_cats
              from category_tags c
                       join list_category l on l.category_id = c.category_id
                       join list_layout ll on ll.layout_id = l.layout_id
              group by 1
              having count(distinct coalesce(ll.user_id, 1)) = 1
                 and count(distinct l.category_id) > 1),
     to_delete as (select tag_id, all_cats[1] as cat from dups)
delete
from category_tags c2
    using to_delete td
where td.tag_id = c2.tag_id
  and td.cat = c2.category_id;