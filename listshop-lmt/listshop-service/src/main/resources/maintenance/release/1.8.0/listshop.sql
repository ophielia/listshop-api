create table if not exists temp_dt as
(
    select
    dish_id,
    tag_id
    from
    dish_tags
    group
    by
    1,
    2
)

begin;
truncate dish_tags;
insert into public.dish_tags (dish_id, tag_id)
select dish_id, tag_id
from temp_dt;

commit;

drop table temp_dt;