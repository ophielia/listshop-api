/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */


insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion, domain_default) values (1059, 'gram', 'UK', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE, TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion, domain_default) values (1060, 'kilogram', 'UK', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE, FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion, domain_default) values (1061, 'milligram', 'UK', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE, FALSE);

insert into factors (factor_id, from_unit, to_unit, factor) select 116, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('gram') and f.type = 'METRIC' and lower(t.name) = lower('gram') and t.type = 'UK';
insert into factors (factor_id, from_unit, to_unit, factor) select 117, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('kilogram') and f.type = 'METRIC' and lower(t.name) = lower('kilogram') and t.type = 'UK';
insert into factors (factor_id, from_unit, to_unit, factor) select 118, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('milligram') and f.type = 'METRIC' and lower(t.name) = lower('milligram') and t.type = 'UK';

update units set domain_default = false;

update units set domain_default = TRUE where unit_id = 1003;
update units set domain_default = TRUE where unit_id = 1007;
update units set domain_default = TRUE where unit_id = 1009;
update units set domain_default = TRUE where unit_id = 1013;
update units set domain_default = TRUE where unit_id = 1026;
update units set domain_default = TRUE where unit_id = 1059;

/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

-- missing factors
insert into factors (factor_id, from_unit, to_unit, factor) select 119, f.unit_id, t.unit_id, 40.0 as factor from units f,units t where lower(f.name) = lower('quart (UK)') and lower(t.name) = lower('fl oz (UK)');
