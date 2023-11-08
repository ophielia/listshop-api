create
or replace view public.admin_user_details
            (user_id, email, user_name, creation_date, last_login, list_count, meal_plan_count, dish_count) as
SELECT u.user_id,
       u.email,
       u.username                     AS user_name,
       u.creation_date,
       u.last_login,
       count(DISTINCT l.list_id)      AS list_count,
       count(DISTINCT m.meal_plan_id) AS meal_plan_count,
       count(DISTINCT d.dish_id)      AS dish_count
FROM users u
         LEFT JOIN list l ON u.user_id = l.user_id
         LEFT JOIN meal_plan m ON u.user_id = m.user_id
         LEFT JOIN dish d ON u.user_id = d.user_id
GROUP BY u.user_id, u.email, u.username, u.creation_date, u.last_login;

alter table public.admin_user_details
    owner to postgres;

create
or replace view public.calculated_stats(tag_id, user_id, frequent_threshold, factored_frequency) as
SELECT list_tag_stats.tag_id,
       list_tag_stats.user_id,
       c.frequent_threshold,
       (list_tag_stats.removed_single * c.removed_single_factor + list_tag_stats.removed_dish * c.removed_dish_factor +
        list_tag_stats.removed_list * c.removed_list_factor +
        list_tag_stats.removed_starterlist * c.removed_starterlist_factor) ::numeric /
       (list_tag_stats.added_single * c.added_single_factor + list_tag_stats.added_dish * c.added_dish_factor +
        list_tag_stats.added_list * c.added_list_factor +
        list_tag_stats.added_starterlist * c.added_starterlist_factor)::numeric AS factored_frequency
FROM list_tag_stats,
     list_stat_configs c
WHERE (list_tag_stats.added_single * c.added_single_factor + list_tag_stats.added_dish * c.added_dish_factor +
       list_tag_stats.added_list * c.added_list_factor +
       list_tag_stats.added_starterlist * c.added_starterlist_factor) > 0;

alter table public.calculated_stats
    owner to postgres;

create
or replace function public.copy_single_dish(integer, integer) returns integer
    language plpgsql
as
$$
DECLARE
pDishId ALIAS for $1;
    pNewUser
ALIAS for $2;
    pDish
record;
    nDish
int;
BEGIN
FOR pDish IN
select *
from Dish o
where o.dish_id = pDishId
    LOOP
insert
into dish (dish_id, description, dish_name, user_id, last_added)
select nextval('dish_sequence'), description, dish_name, pNewUser, last_added
from dish
where dish_id = pDish.dish_id returning dish_id
into nDish;
RAISE
NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
insert into dish_tags (dish_id, tag_id)
select nDish, tag_id
from dish_tags
where dish_id = pDish.dish_id;
END LOOP;
return 1;
END;
$$;

alter function public.copy_single_dish(integer, integer) owner to postgres;

