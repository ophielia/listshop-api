with recursive tag_graph as (
    select t.name, t.tag_id, t.name as parent, tr.parent_tag_id, 0 as depth, t.tag_id as root
    from tag t

             join tag_relation tr on t.tag_id = tr.child_tag_id
    where parent_tag_id is null
      -- and tag_id = 50625
      and t.tag_type = 'Ingredient'
    union
    select t.name,
           t.tag_id,
           p.name as parent,
           tr.parent_tag_id,
           g.depth + 1,
           case
               when g.depth + 1 < 1 then t.tag_id
               else g.root
               end
    from tag t
             join tag_relation tr on t.tag_id = tr.child_tag_id
             join tag p on tr.parent_tag_id = p.tag_id
             join tag_graph g on g.tag_id = tr.parent_tag_id
)
select *
from tag_graph
order by root, depth, parent_tag_id, tag_id



with recursive tag_graph as (
    select t.name, t.tag_id, t.name as parent, tr.parent_tag_id, 0 as depth, t.tag_id as root
    from tag t

             join tag_relation tr on t.tag_id = tr.child_tag_id
    where parent_tag_id is null
      -- and tag_id = 50625
      and t.tag_type = 'Ingredient'
    union
    select t.name,
           t.tag_id,
           p.name as parent,
           tr.parent_tag_id,
           g.depth + 1,
           case
               when g.depth + 1 < 1 then t.tag_id
               else g.root
               end
    from tag t
             join tag_relation tr on t.tag_id = tr.child_tag_id
             join tag p on tr.parent_tag_id = p.tag_id
             join tag_graph g on g.tag_id = tr.parent_tag_id
    --where exists (select from tag_relation where g.tag_id = t.tag_id)
)
select distinct parent, depth, root
from tag_graph
order by root, depth