-- bottle, bag
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains) values (1056, 'bag', 'HYBRID', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'');
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains) values (1057, 'bottle', 'HYBRID', 'WEIGHT',TRUE, TRUE,TRUE,FALSE,'');

-- modifiers
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11700, 'Unit', 'bottles', 'bottle');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11750, 'Unit', 'bottle', 'bottle');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11800, 'Unit', 'bag', 'bag');
insert into modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier) values (11850, 'Unit', 'bags', 'bag');


-- fix some modifier mappings
update modifier_mappings m
set reference_id = u.unit_id
from units u
where m.mapped_modifier = u.name
  and reference_id is null
  and modifier_type = 'Unit';

select * from units where name ilike '%oz%'

update modifier_mappings
set reference_id = 1009
where reference_id is null
  and modifier_type = 'Unit' and mapped_modifier = 'ounce';
