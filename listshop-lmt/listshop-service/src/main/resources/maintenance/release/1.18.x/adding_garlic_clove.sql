-- update table for "single unit" marker
update units set type = 'UNIT', subtype = 'NONE' where  name in ('bulb', 'ear', 'head', 'unit');

-- add sequence for food_conversion
create sequence food_conversion_sequence
    start with 1000;

-- take care of duplicate ids
update food_conversions set food_conversion_id = nextval('food_conversion_sequence');

-- deal with duplicates in food_conversions
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

-- remove all assignments to garlic
update tag
set internal_status = internal_status / 77,
    conversion_id = null
    where conversion_id = 227637;
-- remove factors for garlic
delete from factors where conversion_id = 227637;

-- insert into units - clove
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1055, 'clove', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,TRUE,'', FALSE);

-- insert garlic clove food
insert into foods (food_id, fdc_id, conversion_id, name, category_id, original_name, integral, marker, has_factor) values (107368, 169230, 234820, 'Garlic clove, raw', 11, 'Garlic clove, raw', null, null, true);

-- insert into food_conversions
-- insert clove conversion
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info)
values (234817, 107105, 169230, 227637, 1, null, 'clove', 3, 1055, null, null, null, null, null);
-- insert conversion for new food
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info) values
    (99, 107368, 169230, 234820, 1, null, 'unit', 3, 1055, null, null, true, null, null);
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info) values
    (99, 107368, 169230, 234820, 1, null, 'head', 36, 1039, null, null, false, null, null);
update food_conversions set food_conversion_id = nextval('food_conversion_sequence')
where food_conversion_id = 99 and food_id = 107368;




-- update old unit
update food_conversions
set unit_id = 1039, gram_weight = 36, unit_name = 'head'
where unit_id = 1011 and fdc_id = 169230;

-- mapped_modifiers
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11450, 'Unit', 'cloves', 'clove');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11500, 'Unit', 'toe', 'clove');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11550, 'Unit', 'toes', 'clove');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11600, 'Unit', 'cl.', 'clove');




