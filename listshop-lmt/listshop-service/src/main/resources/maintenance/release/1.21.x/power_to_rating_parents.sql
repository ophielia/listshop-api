update tag t
set power = 5
from tag_relation r
where r.child_tag_id = t.tag_id
  and tag_type = 'Rating'
  and r.parent_tag_id is null;
