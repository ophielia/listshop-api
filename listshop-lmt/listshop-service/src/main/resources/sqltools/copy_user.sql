/*
 * The List Shop
 *
 * Copyright (c) 2022.
 *
 */
drop function if exists copy_dishes;
drop function if exists copy_lists;
drop function if exists copy_meal_plans;
drop function if exists copy_targets;
drop function if exists copy_proposals;
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
insert into dish_items (dish_item_id, dish_id, tag_id)
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

CREATE FUNCTION copy_targets(integer, integer) RETURNS integer
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
                     from target o
                     where o.user_id = pOrigUser
        LOOP

            insert into target (target_id, created, last_updated, last_used, target_name, target_tag_ids, user_id,
                                proposal_id, target, expires, target_type)
            select nextval('target_sequence') as target_id,
                   created,
                   last_updated,
                   last_used,
                   target_name,
                   target_tag_ids,
                   pNewUser                   as user_id,
                   proposal_id,
                   target,
                   expires,
                   target_type
            from target
            where user_id = pOrigUser
              and target_id = pMealPlan.target_id
            returning target_id into nMealPlan;

            RAISE NOTICE 'target created(new:%, old:%)',nMealPlan,pMealPlan.target_id;

            insert into target_slot (target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids, target)
            select nextval('target_slot_sequence') as target_slot_id,
                   slot_dish_tag_id,
                   slot_order,
                   nMealPlan                       as target_id,
                   target_tag_ids,
                   target
            from target_slot
            where target_id = pMealPlan.target_id;


        END LOOP;
    return 1;
END;
$_$;

CREATE FUNCTION copy_proposals(integer, integer) RETURNS integer
    LANGUAGE plpgsql
AS
$_$
DECLARE
    pOrigUser ALIAS for $1;
    pNewUser ALIAS for $2;
    pMealPlan   record;
    pDishSlot   record;
    pSlotRecord record;
    nMealPlan   int;
    nSlotId     int;
BEGIN
    FOR pMealPlan IN select *
                     from proposal o
                     where o.user_id = pOrigUser
        LOOP

            insert into proposal (proposal_id, user_id, is_refreshable, created)
            select nextval('proposal_sequence') as proposal_id,
                   pNewUser                     as user_id,
                   is_refreshable,
                   created
            from proposal
            where user_id = pOrigUser
              and proposal_id = pMealPlan.proposal_id
            returning proposal_id into nMealPlan;

            RAISE NOTICE 'proposal created(new:%, old:%)',nMealPlan,pMealPlan.proposal_id;

            insert into proposal_context (proposal_context_id, proposal_id, current_attempt_index,
                                          current_approach_type, current_approach_index, meal_plan_id, target_id,
                                          target_hash_code, proposal_hash_code)
            select nextval('proposal_context_sequence') as proposal_context_id,
                   nMealPlan                            as proposal_id,
                   current_attempt_index,
                   current_approach_type,
                   current_approach_index,
                   meal_plan_id,
                   target_id,
                   target_hash_code,
                   proposal_hash_code
            from proposal_context
            where proposal_id = pMealPlan.proposal_id;
            raise notice 'ok through here';


            FOR pDishSlot IN select *
                             from proposal_slot o
                             where o.proposal_id = pMealPlan.proposal_id
                LOOP
                    insert into proposal_slot (slot_id, slot_number, flat_matched_tag_ids, proposal_id, picked_dish_id,
                                               slot_dish_tag_id)
                    select nextval('proposal_slot_sequence') as slot_id,
                           slot_number,
                           flat_matched_tag_ids,
                           nMealPlan                         as proposal_id,
                           picked_dish_id,
                           slot_dish_tag_id
                    from proposal_slot
                    where proposal_id = pMealPlan.proposal_id;


                    FOR pSlotRecord in
                        select distinct o.slot_id as origSlotId, n.slot_id as newSlotId
                        from proposal_slot o
                                 join proposal_slot n on n.proposal_id = o.proposal_id
                        where o.proposal_id = pMealPlan.proposal_id
                          and n.proposal_id = nMealPlan
                        LOOP
                            insert into proposal_dish (dish_slot_id, slot_id, dish_id, matched_tag_ids)
                            select nextval('proposal_dish_sequence') as dish_slot_id,
                                   pSlotRecord.newSlotId             as slot_id,
                                   dish_id,
                                   matched_tag_ids
                            from proposal_dish
                            where slot_id = pSlotRecord.origSlotId;
                        END LOOP;


                END LOOP;
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
    perform copy_targets(pOrigUser, pUser.user_id::int);
    perform copy_proposals(pOrigUser, pUser.user_id::int);

    insert into authority (authority_id, name, user_id)
    select nextval('authority_seq') as authority_id,
           name,
           pUser.user_id            as user_id
    from authority
    where user_id = pOrigUser;

    insert into user_devices (user_device_id, user_id, name, model, os, os_version, client_type, build_number,
                              client_device_id, client_version, token, last_login)
    select nextval('user_device_sequence') as user_device_id,
           pUser.user_id                   as user_id,
           name,
           model,
           os,
           os_version,
           client_type,
           build_number,
           client_device_id,
           client_version,
           token,
           last_login
    from user_devices
    where user_id = pOrigUser;

    insert into list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id, added_to_dish,
                                added_single, added_dish, added_list, added_starterlist, removed_single, removed_dish,
                                removed_list, removed_starterlist)
    select nextval('list_tag_stats_sequence') as list_tag_stat_id,
           added_count,
           removed_count,
           tag_id,
           pUser.user_id                      as user_id,
           added_to_dish,
           added_single,
           added_dish,
           added_list,
           added_starterlist,
           removed_single,
           removed_dish,
           removed_list,
           removed_starterlist
    from list_tag_stats
    where user_id = pOrigUser;


    return 1;
END;
$_$;


select copy_user(20, 'bravenewworld@test.com')
into q;

drop table q;

select *
from users
order by user_id desc;


delete
from users
where user_id = 60;