ALTER TABLE public.list_item
ADD COLUMN dish_sources varchar(255);

ALTER TABLE public.list_item
ADD COLUMN list_sources varchar(255);


ALTER TABLE list_item
ADD COLUMN frequent_cross_off boolean default false;



ALTER TABLE list
ADD COLUMN last_update timestamp;



-- ====================
-- ======= data changes
-- ====================

update tag set assign_select = false where tag_id in (
select distinct tag_id from tag t
join tag_relation r on t.tag_id = r.parent_tag_id
left join tag_relation cr on r.parent_tag_id = cr.child_tag_id
and cr.child_tag_id is null
where t.assign_select = true);



create temporary table george as
select distinct pr.name as name
from tag ch
join tag_relation tr on tr.child_tag_id = ch.tag_id
join tag pr on pr.tag_id = tr.parent_tag_id
where ch.assign_select = true
and ch.tag_type in ('Ingredient','NonEdible');



insert into list_category (name, category_id, layout_id, display_order)
select name as name, nextval('list_layout_category_sequence') as category_id, 1 as layout_id , 1 as display_order
from george;


insert into category_tags (category_id, tag_id)
select lc.category_id as category_id,tr.child_tag_id as tag_id
from list_category lc
join tag t on t.name = lc.name
join tag_relation tr on t.tag_id = tr.parent_tag_id
and t.assign_select = false
where lc.layout_id = 1;


drop table george;