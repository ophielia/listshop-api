-- new default user layout
insert into list_layout (layout_id, layout_type, name, user_id, is_default)
values (nextval('list_layout_sequence'), 'RoughGrained', 'Default', 20, true);

-- new category for default user layout
insert into list_category (category_id, name, layout_id, display_order, is_default)
select nextval('list_layout_category_sequence'), 'Other' as name, layout_id, 20, false
from list_layout
where user_id = 20
  and is_default = true;

insert into category_tags (category_id, tag_id)
select category_id, 50532 -- cream cheese
from list_category lc
         join list_layout ll on lc.layout_id = ll.layout_id
where user_id = 20
  and ll.is_default = true
  and lc.name = 'Other';

