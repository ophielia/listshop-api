CREATE OR REPLACE VIEW public.tag_extended AS
  with parent_ids as (select distinct parent_tag_id
                      from tag_relation parent
                      where parent_tag_id is not null)
    SELECT t.tag_id,
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
           r.parent_tag_id,
           cast((ip.parent_tag_id is not null) as bool) as is_parent
    FROM tag t
           JOIN tag_relation r ON t.tag_id = r.child_tag_id
           left join parent_ids ip on t.tag_id = ip.parent_tag_id;