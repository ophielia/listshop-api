
-- remove all assignments to garlic
update tag
set internal_status = internal_status / 77,
    conversion_id = null
where conversion_id = 227637;
-- remove factors for garlic
delete from factors where conversion_id = 227637;

-- insert into food_conversions
-- insert clove conversion
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info)
values (234817, 107105, 169230, 227637, 1, null, 'clove', 3, 1055, null, null, null, null, null);
-- insert conversion for new food
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info) values
    (nextval('food_conversion_sequence'), 107368, 169230, 234820, 1, null, 'unit', 3, 1055, null, null, true, null, null);
insert into food_conversions (food_conversion_id, food_id, fdc_id, conversion_id, amount, marker, unit_name, gram_weight, unit_id, integral, unit_size, unit_default, sub_amount, info) values
    (nextval('food_conversion_sequence'), 107368, 169230, 234820, 1, null, 'head', 36, 1039, null, null, false, null, null);
update food_conversions set food_conversion_id = nextval('food_conversion_sequence')
where food_conversion_id = nextval('food_conversion_sequence') and food_id = 107368;

select * from food_conversions where food_conversion_id = 234817;


-- update old unit
update food_conversions
set unit_id = 1039, gram_weight = 36, unit_name = 'head'
where unit_id = 1011 and fdc_id = 169230;

-- mapped_modifiers
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) values (11450, 'Unit', 'cloves', 'clove', 1055);
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) values (11500, 'Unit', 'toe', 'clove', 1055);
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) values (11550, 'Unit', 'toes', 'clove', 1055);
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) values (11600, 'Unit', 'cl.', 'clove', 1055);
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) values (11650, 'Unit', 'clove', 'clove', 1055);



