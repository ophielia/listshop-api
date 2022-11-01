ALTER TABLE list_layout
    ADD COLUMN user_id bigint;
ALTER TABLE list_layout
    ADD COLUMN is_default boolean;
ALTER TABLE list_layout
    DROP COLUMN layout_type;

ALTER TABLE TAG
    DROP COLUMN assign_select;
ALTER TABLE TAG
    DROP COLUMN search_select;

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

update list_layout
set is_default = true;

update list
set list_layout_id = null;

-- test undo
--ALTER TABLE list_layout DROP COLUMN user_id;
--ALTER TABLE list_layout DROP COLUMN is_default;
--ALTER TABLE list_layout ADD COLUMN layout_type varchar(255);


--create table category_relation
--(
--    category_relation_id bigint not null
--        primary key,
--    child_category_id    bigint
--        constraint category_relation__list_category_id_child
--            references list_category,
--    parent_category_id   bigint
--        constraint category_relation__list_category_id_parent
--            references list_category
--);

-- ALTER TABLE TAG ADD COLUMN assign_select BOOLEAN;
-- ALTER TABLE TAG ADD COLUMN search_select BOOLEAN;