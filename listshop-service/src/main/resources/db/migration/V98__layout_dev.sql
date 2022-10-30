ALTER TABLE list_layout
    ADD COLUMN user_id bigint;
ALTER TABLE list_layout
    ADD COLUMN is_default boolean;


-- delete old entries
delete
from list_category lc
    using list_layout l
where l.layout_id = lc.layout_id
  and l.name <> 'RoughGrained';

-- test undo
--ALTER TABLE list_layout DROP COLUMN user_id;
--ALTER TABLE list_layout DROP COLUMN is_default;


