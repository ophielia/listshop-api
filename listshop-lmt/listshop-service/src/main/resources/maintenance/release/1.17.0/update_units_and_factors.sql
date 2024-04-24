delete from factors;
delete from units;

insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1000, 'cup', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1001, 'tablespoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1002, 'teaspoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1003, 'liter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1004, 'milliliter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1005, 'gallon', 'US', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1006, 'pint', 'US', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1007, 'fl oz', 'US', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1008, 'lb', 'US', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1009, 'oz', 'US', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1010, 'quart', 'US', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1011, 'unit', 'UNIT', 'NONE',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1013, 'gram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1014, 'kilogram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1015, 'centiliter', 'METRIC', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1016, 'milligram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1017, 'cup (fluid)', 'US', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1019, 'teaspoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1021, 'tablespoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1022, 'slice', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1023, 'stick', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1024, 'gallon (UK)', 'UK', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1025, 'pint (UK)', 'UK', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1026, 'fl oz (UK)', 'UK', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1027, 'quart (UK)', 'UK', 'VOLUME',TRUE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1028, 'cup (fluid) (UK)', 'UK', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1029, 'can', 'HYBRID', 'VOLUME',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1030, 'large can', 'HYBRID', 'VOLUME',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1031, 'small can', 'HYBRID', 'VOLUME',TRUE, TRUE,FALSE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1032, '#2 can', 'US', 'VOLUME',FALSE, TRUE,FALSE,FALSE,'', TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1033, '14.5 oz can', 'US', 'VOLUME',FALSE, TRUE,FALSE,FALSE,'', TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1034, '#2.5 can', 'US', 'VOLUME',FALSE, TRUE,FALSE,FALSE,'', TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1035, '#3 can', 'US', 'VOLUME',FALSE, TRUE,FALSE,FALSE,'', TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1036, '29 oz can', 'US', 'VOLUME',FALSE, TRUE,FALSE,FALSE,'', TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1037, 'bulb', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1038, 'ear', 'HYBRID', 'WEIGHT',TRUE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1039, 'head', 'HYBRID', 'WEIGHT',TRUE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1040, 'leaf', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1041, 'package', 'HYBRID', 'WEIGHT',TRUE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1042, 'packet', 'HYBRID', 'WEIGHT',TRUE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1043, 'pod', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1044, 'ring', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1045, 'sheet', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1046, 'spear', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1047, 'sprig', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1048, 'stalk', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1049, 'butter stick', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'METRIC,UK', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1050, 'wedge', 'HYBRID', 'WEIGHT',FALSE, TRUE,FALSE,TRUE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1051, 'teaspoon (fluid) (UK)', 'UK', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid, is_tag_specific, excluded_domains, one_way_conversion) values (1052, 'tablespoon (fluid) (UK)', 'UK', 'VOLUME',FALSE, TRUE,TRUE,FALSE,'', FALSE);


insert into factors (factor_id, from_unit, to_unit, factor) select 1, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('cup (fluid)') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 2, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = lower('cup (fluid)') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 3, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = lower('cup (fluid)') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 4, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = lower('fl oz') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 5, f.unit_id, t.unit_id, 0.0078125 as factor from units f,units t where lower(f.name) = lower('fl oz') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 6, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('fl oz') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 7, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = lower('fl oz') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 8, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = lower('gram') and lower(t.name) = lower('kilogram');
insert into factors (factor_id, from_unit, to_unit, factor) select 9, f.unit_id, t.unit_id, 0.0022 as factor from units f,units t where lower(f.name) = lower('gram') and lower(t.name) = lower('lb');
insert into factors (factor_id, from_unit, to_unit, factor) select 10, f.unit_id, t.unit_id, 0.0352733686067019 as factor from units f,units t where lower(f.name) = lower('gram') and lower(t.name) = lower('oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 11, f.unit_id, t.unit_id, 2.20462262 as factor from units f,units t where lower(f.name) = lower('kilogram') and lower(t.name) = lower('lb');
insert into factors (factor_id, from_unit, to_unit, factor) select 12, f.unit_id, t.unit_id, 35.2733686067019 as factor from units f,units t where lower(f.name) = lower('kilogram') and lower(t.name) = lower('oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 13, f.unit_id, t.unit_id, 100 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('centiliter');
insert into factors (factor_id, from_unit, to_unit, factor) select 14, f.unit_id, t.unit_id, 4.22675 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 15, f.unit_id, t.unit_id, 0.26417 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 16, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('milliliter');
insert into factors (factor_id, from_unit, to_unit, factor) select 17, f.unit_id, t.unit_id, 2.1133764 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 18, f.unit_id, t.unit_id, 1.05668821 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 19, f.unit_id, t.unit_id, 0.00422675 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 20, f.unit_id, t.unit_id, 0.00026417 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 21, f.unit_id, t.unit_id, 0.0021133764 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 22, f.unit_id, t.unit_id, 0.0010566882 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 23, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = lower('pint') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 24, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = lower('pint') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 25, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = lower('quart') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 26, f.unit_id, t.unit_id, 0.0000022 as factor from units f,units t where lower(f.name) = lower('milligram') and lower(t.name) = lower('lb');
insert into factors (factor_id, from_unit, to_unit, factor) select 27, f.unit_id, t.unit_id, 0.00003527 as factor from units f,units t where lower(f.name) = lower('milligram') and lower(t.name) = lower('oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 28, f.unit_id, t.unit_id, 0.0105668821 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 29, f.unit_id, t.unit_id, 0.0422675 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 30, f.unit_id, t.unit_id, 0.0026417205124156 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 31, f.unit_id, t.unit_id, 0.021133764 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 32, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('liter');
insert into factors (factor_id, from_unit, to_unit, factor) select 33, f.unit_id, t.unit_id, 0.1 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('centiliter');
insert into factors (factor_id, from_unit, to_unit, factor) select 34, f.unit_id, t.unit_id, 0.01 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('liter');
insert into factors (factor_id, from_unit, to_unit, factor) select 35, f.unit_id, t.unit_id, 10 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('milliliter');
insert into factors (factor_id, from_unit, to_unit, factor) select 36, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('oz') and lower(t.name) = lower('lb');
insert into factors (factor_id, from_unit, to_unit, factor) select 37, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = lower('lb') and lower(t.name) = lower('oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 38, f.unit_id, t.unit_id, 0.3333333333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('tablespoon (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 39, f.unit_id, t.unit_id, 0.33333333333 as factor from units f,units t where lower(f.name) = lower('teaspoon') and lower(t.name) = lower('tablespoon');
insert into factors (factor_id, from_unit, to_unit, factor) select 40, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = lower('teaspoon') and lower(t.name) = lower('cup');
insert into factors (factor_id, from_unit, to_unit, factor) select 41, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 42, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = lower('teaspoon') and lower(t.name) = lower('cup');
insert into factors (factor_id, from_unit, to_unit, factor) select 43, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('tablespoon') and lower(t.name) = lower('cup');
insert into factors (factor_id, from_unit, to_unit, factor) select 44, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('cup (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 45, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = lower('tablespoon') and lower(t.name) = lower('teaspoon');
insert into factors (factor_id, from_unit, to_unit, factor) select 46, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('teaspoon (fluid)');
insert into factors (factor_id, from_unit, to_unit, factor) select 47, f.unit_id, t.unit_id, 0.00390625 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 48, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 49, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 50, f.unit_id, t.unit_id, 0.015625 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid)') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 51, f.unit_id, t.unit_id, 0.00130208333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('gallon');
insert into factors (factor_id, from_unit, to_unit, factor) select 52, f.unit_id, t.unit_id, 0.0104166666666 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('pint');
insert into factors (factor_id, from_unit, to_unit, factor) select 53, f.unit_id, t.unit_id, 0.1666666666 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 54, f.unit_id, t.unit_id, 0.00520833333333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid)') and lower(t.name) = lower('quart');
insert into factors (factor_id, from_unit, to_unit, factor) select 55, f.unit_id, t.unit_id, 128 as factor from units f,units t where lower(f.name) = lower('gallon') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 56, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = lower('pint') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 57, f.unit_id, t.unit_id, 8.0 as factor from units f,units t where lower(f.name) = lower('cup (fluid)') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 58, f.unit_id, t.unit_id, 33.8140227 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 59, f.unit_id, t.unit_id, 0.033814023 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 60, f.unit_id, t.unit_id, 0.33814203 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('fl oz');
insert into factors (factor_id, from_unit, to_unit, factor) select 61, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = lower('kilogram') and lower(t.name) = lower('gram');
insert into factors (factor_id, from_unit, to_unit, factor) select 62, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = lower('cup') and lower(t.name) = lower('tablespoon');
insert into factors (factor_id, from_unit, to_unit, factor) select 63, f.unit_id, t.unit_id, 48 as factor from units f,units t where lower(f.name) = lower('cup') and lower(t.name) = lower('teaspoon');
insert into factors (factor_id, from_unit, to_unit, factor) select 64, f.unit_id, t.unit_id, 3.5195079727854 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('cup (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 65, f.unit_id, t.unit_id, 0.21996924829909 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 66, f.unit_id, t.unit_id, 1.7597539863927 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 67, f.unit_id, t.unit_id, 0.87987699319635 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 68, f.unit_id, t.unit_id, 0.00021996924829909 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 69, f.unit_id, t.unit_id, 0.0017597539863927 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 70, f.unit_id, t.unit_id, 0.00087987699319635 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 71, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = lower('pint (UK)') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 72, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = lower('cup (fluid) (UK)') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 73, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = lower('quart (UK)') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 74, f.unit_id, t.unit_id, 0.0087987699319635 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 75, f.unit_id, t.unit_id, 0.035195079727854 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('cup (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 76, f.unit_id, t.unit_id, 0.0021996924829909 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 77, f.unit_id, t.unit_id, 0.017597539863927 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 78, f.unit_id, t.unit_id, 0.020833333333333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid) (UK)') and lower(t.name) = lower('cup (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 79, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid) (UK)') and lower(t.name) = lower('cup (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 80, f.unit_id, t.unit_id, 0.003125 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid) (UK)') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 81, f.unit_id, t.unit_id, 0.02500001 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid) (UK)') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 82, f.unit_id, t.unit_id, 0.625 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid) (UK)') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 83, f.unit_id, t.unit_id, 0.0125 as factor from units f,units t where lower(f.name) = lower('tablespoon (fluid) (UK)') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 84, f.unit_id, t.unit_id, 0.00104167 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid) (UK)') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 85, f.unit_id, t.unit_id, 0.00833334 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid) (UK)') and lower(t.name) = lower('pint (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 86, f.unit_id, t.unit_id, 0.208333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid) (UK)') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 87, f.unit_id, t.unit_id, 0.00520833333333 as factor from units f,units t where lower(f.name) = lower('teaspoon (fluid) (UK)') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 88, f.unit_id, t.unit_id, 160 as factor from units f,units t where lower(f.name) = lower('gallon (UK)') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 89, f.unit_id, t.unit_id, 20 as factor from units f,units t where lower(f.name) = lower('pint (UK)') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 90, f.unit_id, t.unit_id, 10 as factor from units f,units t where lower(f.name) = lower('cup (fluid) (UK)') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 91, f.unit_id, t.unit_id, 35.195079727854 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 92, f.unit_id, t.unit_id, 0.035195079727854 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 93, f.unit_id, t.unit_id, 0.35195079727854 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('fl oz (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 94, f.unit_id, t.unit_id, 56.312127564566 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('tablespoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 95, f.unit_id, t.unit_id, 0.56312127564566 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('tablespoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 96, f.unit_id, t.unit_id, 0.056312127564567 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('tablespoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 97, f.unit_id, t.unit_id, 168.9363826937 as factor from units f,units t where lower(f.name) = lower('liter') and lower(t.name) = lower('teaspoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 98, f.unit_id, t.unit_id, 1.689363826937 as factor from units f,units t where lower(f.name) = lower('centiliter') and lower(t.name) = lower('teaspoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 99, f.unit_id, t.unit_id, 0.1689363826937 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('teaspoon (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 100, f.unit_id, t.unit_id, 0.0035195079727854 as factor from units f,units t where lower(f.name) = lower('milliliter') and lower(t.name) = lower('cup (fluid) (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 101, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = lower('cup (fluid) (UK)') and lower(t.name) = lower('gallon (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 102, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = lower('cup (fluid) (UK)') and lower(t.name) = lower('quart (UK)');
insert into factors (factor_id, from_unit, to_unit, factor) select 103, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('#2 can') and lower(t.name) = lower('can');
insert into factors (factor_id, from_unit, to_unit, factor) select 104, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('14.5 oz can') and lower(t.name) = lower('can');
insert into factors (factor_id, from_unit, to_unit, factor) select 105, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('#2.5 can') and lower(t.name) = lower('large can');
insert into factors (factor_id, from_unit, to_unit, factor) select 106, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('#3 can') and lower(t.name) = lower('large can');
insert into factors (factor_id, from_unit, to_unit, factor) select 107, f.unit_id, t.unit_id, 1 as factor from units f,units t where lower(f.name) = lower('29 oz can') and lower(t.name) = lower('large can');



select * from factors where from_unit in (1025,1028) or to_unit in (1025,1028) ;

select * from factors where
(from_unit = 1003 and to_unit = 1024) or
(to_unit = 1024 and from_unit = 1003);

select * from units where unit_id = 1024
