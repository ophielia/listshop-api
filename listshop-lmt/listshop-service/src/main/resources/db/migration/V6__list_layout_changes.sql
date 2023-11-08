ALTER TABLE list_layout
    ADD COLUMN user_id bigint;
ALTER TABLE list_layout
    ADD COLUMN is_default boolean;
ALTER TABLE list_layout
DROP
COLUMN layout_type;

ALTER TABLE TAG
DROP
COLUMN assign_select;
ALTER TABLE TAG
DROP
COLUMN search_select;

-- delete old entries
truncate table category_relation;

delete
from category_tags c using list_layout l, list_category lc
where l.layout_id = lc.layout_id
  and l.name <> 'RoughGrained'
  and lc.category_id = c.category_id;

delete
from list_category lc using list_layout l
where l.layout_id = lc.layout_id
  and l.name <> 'RoughGrained';

delete
from list_layout l
where l.name <> 'RoughGrained';


drop table category_relation;
drop view tag_extended;

update list_layout
set is_default = true;

update list
set list_layout_id = null;

