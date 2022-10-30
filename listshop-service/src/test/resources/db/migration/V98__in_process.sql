ALTER TABLE list_layout
    ADD COLUMN user_id bigint;
ALTER TABLE list_layout
    ADD COLUMN is_default boolean;


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


drop table category_relation;

-- test undo
--ALTER TABLE list_layout DROP COLUMN user_id;
--ALTER TABLE list_layout DROP COLUMN is_default;


