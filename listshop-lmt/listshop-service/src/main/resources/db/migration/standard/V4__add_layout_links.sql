/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

ALTER TABLE list_layout ADD COLUMN linked_layout_id bigint;
ALTER TABLE list_category ADD COLUMN linked_category_id bigint;

update list_layout set linked_layout_id = 5 where user_id is not null;

with base_layouts as (select *
                      from list_category
                      where layout_id = 5),
     update_mapping as (select b.layout_id as linked_id, b.category_id, us.name, us.category_id as to_update, ll.user_id
                        from base_layouts b
                                 join list_category us on us.name = b.name
                                 join list_layout ll on ll.layout_id = us.layout_id
                        where ll.layout_id <> 5
                          and ll.user_id is not null)
update list_category lc
set linked_category_id = um.category_id
from update_mapping um
where um.to_update = lc.category_id;
