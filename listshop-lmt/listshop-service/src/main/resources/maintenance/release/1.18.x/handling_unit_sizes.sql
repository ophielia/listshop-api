-- update table for "single unit" marker
update units set type = 'UNIT', subtype = 'NONE' where  name in ('bulb', 'ear', 'head', 'unit');

-- fill in all unit sizes
update food_conversions set unit_size = 'medium' where unit_size is null;

-- take care of duplicates in food_conversion
with dups as (
    select count(*) ,conversion_id, food_id, fdc_id, amount, unit_name, gram_weight,
           unit_id,  marker, sub_amount, info, unit_size, unit_default
    from food_conversions
    group by 2,3,4,5,6,7,8,9,10,11,12,13 having count(*) > 1),
     the_ids as (
--select array_agg(food_conversion_id)
         select array_agg(f.food_conversion_id) as ids,f.conversion_id,f.food_id,f.fdc_id,f.amount,f.unit_name,f.gram_weight,
                f.unit_id,f. marker,f.sub_amount,f.info,f.unit_size,f.unit_default
         from food_conversions f join dups d
                                      on d.conversion_id = f.conversion_id
                                          and  d.food_id = f.food_id
                                          and d.fdc_id = f.fdc_id
                                          and d.amount = f.amount
                                          and ((d.unit_name is null and f.unit_name is null) OR trim(lower(d.unit_name)) = trim(lower(f.unit_name)))
                                          and d.gram_weight = f.gram_weight
                                          and d.unit_id = f.unit_id
                                          and ((d.marker is null and f.marker is null) OR trim(lower(d.marker)) = trim(lower(f.marker)))
                                          and ((d.sub_amount is null and f.sub_amount is null) OR trim(lower(d.sub_amount)) = trim(lower(f.sub_amount)))
                                          and ((d.info is null and f.info is null) OR trim(lower(d.info)) = trim(lower(f.info)))
                                          and ((d.unit_size is null and f.unit_size is null) OR trim(lower(d.unit_size)) = trim(lower(f.unit_size)))
                                          and ((d.unit_default is null and f.unit_default is null) OR d.unit_default = f.unit_default)
         group by 2,3,4,5,6,7,8,9,10,11,12,13),
     to_del as (select ids[1] as delid from the_ids)
delete from food_conversions f
    using to_del d
where d.delid = f.food_conversion_id;

-- take care of duplicates - duplicate gram weight - in food_conversion
with dups as (
    select count(*) ,conversion_id, food_id, fdc_id, amount, unit_name, gram_weight,
           unit_id,  marker, sub_amount,  unit_size, unit_default, array_agg(info) as me
    from food_conversions
    group by 2,3,4,5,6,7,8,9,10,11,12 having count(*) > 1),
     the_ids as (
--select array_agg(food_conversion_id)
         select array_agg(f.food_conversion_id) as ids,f.conversion_id,f.food_id,f.fdc_id,f.amount,f.unit_name,f.gram_weight,
                f.unit_id,f. marker,f.sub_amount,f.info,f.unit_size,f.unit_default
         from food_conversions f join dups d
                                      on d.conversion_id = f.conversion_id
                                          and  d.food_id = f.food_id
                                          and d.fdc_id = f.fdc_id
                                          and d.amount = f.amount
                                          and ((d.unit_name is null and f.unit_name is null) OR trim(lower(d.unit_name)) = trim(lower(f.unit_name)))
                                          and d.gram_weight = f.gram_weight
                                          and d.unit_id = f.unit_id
                                          and ((d.marker is null and f.marker is null) OR trim(lower(d.marker)) = trim(lower(f.marker)))
                                          and ((d.sub_amount is null and f.sub_amount is null) OR trim(lower(d.sub_amount)) = trim(lower(f.sub_amount)))
                                          and ((d.unit_size is null and f.unit_size is null) OR trim(lower(d.unit_size)) = trim(lower(f.unit_size)))
                                          and ((d.unit_default is null and f.unit_default is null) OR d.unit_default = f.unit_default)
         group by 2,3,4,5,6,7,8,9,10,11,12,13),
     to_del as (select ids[1] as delid from the_ids)
delete from food_conversions f using to_del d
where d.delid = f.food_conversion_id
and f.info is null;



-- move smallest unit size of multiples to small
with finder as (select conversion_id,
                       unit_size,
                       f.unit_id,
                       count(*),
                       array_agg(food_conversion_id) as ids,
                       min(gram_weight)              as size
                from food_conversions f
                         join units u on f.unit_id = u.unit_id
                where u.type = 'UNIT'
                  and unit_size = 'medium'
                group by 1, 2, 3
                having count(*) > 1),
     exp as (select unnest(ids) as dup_ids from finder)
update food_conversions f
set unit_size = 'small'
from exp e,
     finder d
where e.dup_ids = f.food_conversion_id
  and d.conversion_id = f.conversion_id
  and d.unit_id = f.unit_id
  and d.unit_size = f.unit_size
  and d.size = f.gram_weight;


-- move largest unit size of multiples to large
with finder as (select conversion_id,
                       unit_size,
                       f.unit_id,
                       count(*),
                       array_agg(food_conversion_id) as ids,
                       max(gram_weight)              as size
                from food_conversions f
                         join units u on f.unit_id = u.unit_id
                where u.type = 'UNIT'
                  and unit_size = 'medium'
                group by 1, 2, 3
                having count(*) > 1),
     exp as (select unnest(ids) as dup_ids from finder)
update food_conversions f
set unit_size = 'large'
from exp e,
     finder d
where e.dup_ids = f.food_conversion_id
  and d.conversion_id = f.conversion_id
  and d.unit_id = f.unit_id
  and d.unit_size = f.unit_size
  and d.size = f.gram_weight;


-- check for remaining size dups
with finder as (
    select conversion_id, unit_size, f.unit_id, count(*), array_agg(food_conversion_id) as ids,
           max(gram_weight) as size
    from food_conversions f
             join units u on f.unit_id = u.unit_id
    where u.type = 'UNIT'
      and unit_size = 'medium'
    group by 1,2,3 having count(*) > 1),
     exp as (select unnest(ids) as dup_ids from finder)
select count(*) from finder;


-- add sequence for food_conversion
create sequence if not exists food_conversion_sequence
    start with 1000;

-- take care of duplicate ids
update food_conversions set food_conversion_id = nextval('food_conversion_sequence');

