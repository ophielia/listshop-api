-- reset autotags
-- takes chicken stock out of meat
update tag_relation
set parent_tag_id = 379
where child_tag_id = 437


delete
from shadow_tags;
update dish
set auto_tag_status = null;
