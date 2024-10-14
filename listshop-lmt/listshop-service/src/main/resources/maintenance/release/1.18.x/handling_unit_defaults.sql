-- fill in unit_default for all
update food_conversions set unit_default = false where unit_default is null;

-- check for duplicate defaults
select conversion_id, unit_id, count(*), ARRay_agg(food_conversion_id) as dup_ids
from food_conversions where unit_default = true
group by 1,2 having count(*) >1;

-- fix mediums
with dups as (
    select fc.conversion_id, fc.unit_id, count(*), ARRay_agg(food_conversion_id) as dup_ids
    from food_conversions fc
             join units u on u.unit_id = fc.unit_id
    where unit_default = true
    --and u.type = 'UNIT'
    group by 1,2 having count(*) >1
),
    dup_ids as (select unnest(dup_ids) as id from dups)
update food_conversions fc
    set unit_default = false
from dup_ids d where d.id = fc.food_conversion_id
and unit_size <> 'medium';

with dups as (
    select conversion_id, u.unit_id, marker,count(*), array_agg(unit_size),ARRay_agg(food_conversion_id) as dup_ids
    from food_conversions fc
    join units u on u.unit_id = fc.unit_id
    where unit_default = true
    --and u.type = 'UNIT'
    group by 1,2,3 having count(*) >1
),
     dup_ids as (select unnest(dup_ids) as id from dups)
select * from food_conversions fc
join dup_ids d on d.id = fc.food_conversion_id;

