-- remove tag_search_group
-- drop table if exists tag_search_group;

-- add column is_group to tags
alter table tag
    add column is_group boolean NOT NULL default false;
update tag t
set is_group = true from tag_relation tr
where t.tag_id = tr.parent_tag_id;

alter table tag
    add column user_id bigint;


drop view if exists tag_extended;
create view tag_extended
            (tag_id, user_id, is_group, assign_select, category_updated_on, created_on, description, is_verified, name,
             power, removed_on,
             replacement_tag_id, search_select, tag_type, tag_type_default, to_delete, updated_on, parent_tag_id,
             is_parent)
as
WITH parent_ids AS (SELECT DISTINCT parent.parent_tag_id
                    FROM tag_relation parent
                    WHERE parent.parent_tag_id IS NOT NULL)
SELECT t.tag_id,
       t.user_id,
       t.is_group,
       false,
       t.category_updated_on,
       t.created_on,
       t.description,
       t.is_verified,
       t.name,
       t.power,
       t.removed_on,
       t.replacement_tag_id,
       false,
       t.tag_type,
       t.tag_type_default,
       t.to_delete,
       t.updated_on,
       r.parent_tag_id,
       ip.parent_tag_id IS NOT NULL AS is_parent
FROM tag t
         JOIN tag_relation r ON t.tag_id = r.child_tag_id
         LEFT JOIN parent_ids ip ON t.tag_id = ip.parent_tag_id;

-- admin_user_details
create
or replace view admin_user_details as
(
select u.user_id,
       email,
       username                       as user_name,
       creation_date,
       last_login,
       count(distinct list_id)        as list_count,
       count(distinct m.meal_plan_id) as meal_plan_count,
       count(distinct dish_id)        as dish_count
from users u
         left outer join list l on u.user_id = l.user_id
         left outer join meal_plan m on u.user_id = m.user_id
         left outer join dish d on u.user_id = d.user_id
group by 1, 2, 3, 4, 5
    );


-- rollback
-- re-add tag_search_group
-- create table tag_search_group
-- (
--     tag_search_group_id bigint not null
--         primary key,
--     group_id            bigint,
--     member_id           bigint
-- );


--drop  view if exists tag_extended;
--CREATE OR REPLACE VIEW public.tag_extended AS WITH parent_ids AS (SELECT DISTINCT parent.parent_tag_id FROM public.tag_relation parent WHERE (parent.parent_tag_id IS NOT NULL)) SELECT t.tag_id,t.assign_select,t.category_updated_on,t.created_on,t.description,t.is_verified,t.name,t.power,t.removed_on,t.replacement_tag_id,t.search_select,t.tag_type,t.tag_type_default,t.to_delete,t.updated_on,r.parent_tag_id,(ip.parent_tag_id IS NOT NULL) AS is_parent FROM ((public.tag t JOIN public.tag_relation r ON ((t.tag_id = r.child_tag_id)))LEFT JOIN parent_ids ip ON ((t.tag_id = ip.parent_tag_id)));
-- GRANT ALL ON TABLE tag_extended TO bankuser;
-- alter table tag_extended    owner to bank;
-- remove new columns from tag
--alter table tag drop column is_group;
--alter table tag drop column user_id;
-- drop view if exists admin_user_details;