/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */
drop function if exists copy_single_dish;

drop table if exists q;

CREATE FUNCTION copy_single_dish(integer, integer) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
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
insert into dish_items (dish_item_id, dish_id, tag_id)
select nDish, tag_id
from dish_tags
where dish_id = pDish.dish_id;
END LOOP;
return 1;
END;
$_$;

select copy_single_dish(56630, 34)
into q;



select *
from dish
where user_id = 34;