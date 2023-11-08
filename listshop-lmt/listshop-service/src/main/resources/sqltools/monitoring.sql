select *
from users;

select email, l.name, count(*)
from users u
         join list l using (user_id)
         join list_item i using (list_id)
group by email, l.name
order by email
;

select email, count(*)
from users u
         join list l using (user_id)
group by email
order by email
;

select *
from tag
order by tag_id desc