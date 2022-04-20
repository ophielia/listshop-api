-- remove tag_search_group
-- drop table if exists tag_search_group;

-- add column is_group to tags
alter table tag
    add column is_group boolean default false;
alter table tag
    alter column is_group SET NOT NULL;
update tag t
set is_group = true from tag_relation tr
where t.tag_id = tr.parent_tag_id;

alter table tag
    add column user_id bigint;



-- rollback
-- re-add tag_search_group
-- create table tag_search_group
-- (
--     tag_search_group_id bigint not null
--         primary key,
--     group_id            bigint,
--     member_id           bigint
-- );

-- remove new columns from tag
--alter table tag drop column is_group;
--alter table tag drop column user_id;