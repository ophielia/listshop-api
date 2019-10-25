-- LISTSHOP-236 changes --

-- new view
CREATE OR REPLACE VIEW public.tag_extended AS
select t.tag_id,
       t.assign_select,
       t.category_updated_on,
       t.created_on,
       t.description,
       t.is_verified,
       t.name,
       t.power,
       t.removed_on,
       t.replacement_tag_id,
       t.search_select,
       t.tag_type,
       t.tag_type_default,
       t.to_delete,
       t.updated_on,
       r.parent_tag_id
from tag t
       join tag_relation r on t.tag_id = r.child_tag_id;

ALTER TABLE public.tag_extended
  OWNER TO postgres;

-- fixing tags - assign_select
update tag
set assign_select = false
where tag_id in (
  select distinct t.tag_id
  from tag t
         join tag_relation tr on t.tag_id = tr.parent_tag_id
    and assign_select = true);

-- fixing autotag
update auto_tag_instructions
set instruction_type = upper(instruction_type);

INSERT INTO auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms)
VALUES ('TAG', nextval('auto_tag_instructions_sequence'), 346, false, '9;88;368;372;374;375');


-- default column for listLayoutCategory
alter table list_category
  add column is_default boolean;

-- insert default categories for lists
insert into list_category (category_id, name, layout_id, is_default, display_order)
select nextval('list_layout_category_sequence') as category_id,
       'Not (yet) categorized'                  as name,
       layout_id,
       true                                     as is_default,
       999                                      as display_order
from list_layout;

-- LISTSHOP-238 changes
update list_tag_stats
set added_to_dish = 0
where added_to_dish is null;

ALTER TABLE list_tag_stats
  ALTER COLUMN added_to_dish SET DEFAULT 0;

update list_tag_stats
set added_to_dish = 0
where added_to_dish is null;

ALTER TABLE list_tag_stats
  ALTER COLUMN added_to_dish SET DEFAULT 0;

-- LISTSHOP-236 rollback --

-- drop view public.tag_extended;

--alter table list_category
--   drop column is_default;

-- delete from list_category where display_order = 999;


-- LISTSHOP-238 rollback --
--ALTER TABLE list_tag_stats
--     ALTER COLUMN added_to_dish SET DEFAULT null;

--ALTER TABLE list_tag_stats
--     ALTER COLUMN added_count SET DEFAULT null;