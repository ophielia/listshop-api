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
drop view tag_extended;

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

-- create view tag_extended
--             (tag_id, user_id, is_group, assign_select, category_updated_on, created_on, description, is_verified, name,
--              power, removed_on, replacement_tag_id, search_select, tag_type, tag_type_default, to_delete, updated_on,
--              parent_tag_id, is_parent)
-- as
-- WITH parent_ids AS (SELECT DISTINCT parent.parent_tag_id
--                     FROM tag_relation parent
--                     WHERE parent.parent_tag_id IS NOT NULL)
-- SELECT t.tag_id,
--        t.user_id,
--        t.is_group,
--        false                        AS assign_select,
--        t.category_updated_on,
--        t.created_on,
--        t.description,
--        t.is_verified,
--        t.name,
--        t.power,
--        t.removed_on,
--        t.replacement_tag_id,
--        false                        AS search_select,
--        t.tag_type,
--        t.tag_type_default,
--        t.to_delete,
--        t.updated_on,
--        r.parent_tag_id,
--        ip.parent_tag_id IS NOT NULL AS is_parent
-- FROM tag t
--          JOIN tag_relation r ON t.tag_id = r.child_tag_id
--          LEFT JOIN parent_ids ip ON t.tag_id = ip.parent_tag_id;
--
-- alter table tag_extended
--     owner to bank;