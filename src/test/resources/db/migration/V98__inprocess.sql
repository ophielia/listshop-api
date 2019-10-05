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


-- LISTSHOP-236 rollback --