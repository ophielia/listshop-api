--- new user, and single list
delete
from users
where user_id in (101010);
delete
from tag_relation
where child_tag_id in (999999, 88888);
delete
from tag
where tag_id in (999999, 88888);
