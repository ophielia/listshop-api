/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */
drop function if exists copy_dishes;
drop function if exists copy_lists;
drop function if exists copy_meal_plans;
drop function if exists copy_user;


CREATE FUNCTION copy_dishes(integer, integer) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
DECLARE
    pOrigUser ALIAS for $1;
    pNewUser ALIAS for $2;
    pDish record;
    nDish int;
BEGIN
    FOR pDish IN select *
                 from Dish o
                          left join dish n on o.dish_name = n.dish_name and n.user_id = pNewUser
                 where o.user_id = pOrigUser
                   and n.dish_id is null
        LOOP
            insert into dish (dish_id, description, dish_name, user_id, last_added)
            select nextval('dish_sequence'), description, dish_name, pNewUser, last_added
            from dish
            where user_id = pOrigUser
              and dish_id = pDish.dish_id
            returning dish_id into nDish;
            RAISE NOTICE 'dish created(new:%, old:%)',nDish,pDish.dish_id;
            insert into dish_tags (dish_id, tag_id)
            select nDish, tag_id
            from dish_tags
            where dish_id = pDish.dish_id;
        END LOOP;
    return 1;
END;
$_$;

CREATE FUNCTION copy_lists(integer, integer) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
DECLARE
    pOrigUser ALIAS for $1;
    pNewUser ALIAS for $2;
    pList record;
    nList int;
BEGIN
    FOR pList IN select *
                 from list o
                 where o.user_id = pOrigUser
        LOOP
            insert into list (list_id, created_on, user_id, list_layout_id, last_update, is_starter_list, name)
            select nextval('list_sequence'),
                   l.created_on,
                   pNewUser as user_id,
                   list_layout_id,
                   last_update,
                   is_starter_list,
                   name
            from list l
            where user_id = pOrigUser
              and list_id = pList.list_id
            returning list_id into nList;

            RAISE NOTICE 'list created(new:%, old:%)',nList,pList.list_id;

            insert into list_item (item_id, added_on, crossed_off, source, list_id, tag_id, used_count, dish_sources,
                                   list_sources, removed_on, updated_on)
            select nextval('list_item_sequence'),
                   added_on,
                   crossed_off,
                   source,
                   nList,
                   tag_id,
                   used_count,
                   dish_sources,
                   list_sources,
                   removed_on,
                   updated_on
            from list_item
            where list_id = pList.list_id;

        END LOOP;
    return 1;
END;
$_$;

CREATE FUNCTION copy_meal_plans(integer, integer) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
DECLARE
    pOrigUser ALIAS for $1;
    pNewUser ALIAS for $2;
    pMealPlan record;
    nMealPlan int;
BEGIN
    FOR pMealPlan IN select *
                     from meal_plan o
                     where o.user_id = pOrigUser
        LOOP

            insert into meal_plan (meal_plan_id, created, meal_plan_type, name, user_id, target_id)
            select nextval('meal_plan_sequence') as meal_plan_id,
                   created,
                   meal_plan_type,
                   name,
                   pNewUser                      as user_id,
                   target_id
            from meal_plan
            where user_id = pOrigUser
              and meal_plan_id = pMealPlan.meal_plan_id
            returning meal_plan_id into nMealPlan;

            RAISE NOTICE 'meal plan created(new:%, old:%)',nMealPlan,pMealPlan.meal_plan_id;

            insert into meal_plan_slot (meal_plan_slot_id, dish_dish_id, meal_plan_id)
            select nextval('meal_plan_slot_sequence'),
                   dish_dish_id,
                   nMealPlan
            from meal_plan_slot
            where meal_plan_id = pMealPlan.meal_plan_id;

        END LOOP;
    return 1;
END;
$_$;

CREATE FUNCTION copy_user(integer, varchar) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
DECLARE
    pOrigUser ALIAS for $1;
    pNewUserName ALIAS for $2;
    pUser record;
BEGIN

    insert into users (user_id, email, enabled, last_password_reset_date, password, username, creation_date, last_login)
    select nextval('user_id_sequence') as user_id,
           pNewUserName                as email,
           enabled,
           last_password_reset_date,
           password,
           pNewUserName                as username,
           creation_date,
           last_login
    from users
    where user_id = pOrigUser
    returning user_id into pUser;

    RAISE NOTICE 'user created(new:%, old:%)',pOrigUser,pUser.user_id;

    perform copy_dishes(pOrigUser, pUser.user_id::int);
    perform copy_lists(pOrigUser, pUser.user_id::int);
    perform copy_meal_plans(pOrigUser, pUser.user_id::int);

    return 1;
END;
$_$;


select copy_user(20, 'usertodelete6@test.com')
into q;

drop table q;
