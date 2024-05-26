select * from factors;

insert into factors (factor_id, from_unit, to_unit, factor) select 1, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 2, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 3, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 4, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 5, f.unit_id, t.unit_id, 0.0078125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 6, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 7, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 8, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'kilogram';
insert into factors (factor_id, from_unit, to_unit, factor) select 9, f.unit_id, t.unit_id, 0.0022 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select 10, f.unit_id, t.unit_id, 0.0352733686067019 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 11, f.unit_id, t.unit_id, 2.20462262 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select 12, f.unit_id, t.unit_id, 35.2733686067019 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 13, f.unit_id, t.unit_id, 100 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor) select 14, f.unit_id, t.unit_id, 4.22675 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 15, f.unit_id, t.unit_id, 0.26417 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 16, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor) select 17, f.unit_id, t.unit_id, 2.1133764 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 18, f.unit_id, t.unit_id, 1.05668821 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 19, f.unit_id, t.unit_id, 0.00422675 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 20, f.unit_id, t.unit_id, 0.00026417 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 21, f.unit_id, t.unit_id, 0.0021133764 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 22, f.unit_id, t.unit_id, 0.0010566882 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 23, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 24, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 25, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = 'quart' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 26, f.unit_id, t.unit_id, 0.0000022 as factor from units f,units t where lower(f.name) = 'milligram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select 27, f.unit_id, t.unit_id, 0.00003527 as factor from units f,units t where lower(f.name) = 'milligram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 28, f.unit_id, t.unit_id, 0.0105668821 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 29, f.unit_id, t.unit_id, 0.0422675 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 30, f.unit_id, t.unit_id, 0.0026417205124156 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 31, f.unit_id, t.unit_id, 0.021133764 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 32, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor) select 33, f.unit_id, t.unit_id, 0.1 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor) select 34, f.unit_id, t.unit_id, 0.01 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor) select 35, f.unit_id, t.unit_id, 10 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor) select 36, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'oz' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select 37, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = 'lb' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 38, f.unit_id, t.unit_id, 0.3333333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'tablespoon (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 39, f.unit_id, t.unit_id, 0.33333333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'tablespoon';
insert into factors (factor_id, from_unit, to_unit, factor) select 40, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select 41, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 42, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select 43, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'tablespoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select 44, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 45, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = 'tablespoon' and lower(t.name) = 'teaspoon';
insert into factors (factor_id, from_unit, to_unit, factor) select 46, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'teaspoon (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select 47, f.unit_id, t.unit_id, 0.00390625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 48, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 49, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 50, f.unit_id, t.unit_id, 0.015625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 51, f.unit_id, t.unit_id, 0.00130208333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select 52, f.unit_id, t.unit_id, 0.0104166666666 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select 53, f.unit_id, t.unit_id, 0.1666666666 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 54, f.unit_id, t.unit_id, 0.00520833333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select 55, f.unit_id, t.unit_id, 0.0078125 as factor from units f,units t where lower(f.name) = 'gallon' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 56, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 57, f.unit_id, t.unit_id, 8.0 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 58, f.unit_id, t.unit_id, 33.8140227 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 59, f.unit_id, t.unit_id, 0.033814023 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 60, f.unit_id, t.unit_id, 0.33814203 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select 61, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'gram';
insert into factors (factor_id, from_unit, to_unit, factor) select 62, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = 'cup' and lower(t.name) = 'tablespoon';
insert into factors (factor_id, from_unit, to_unit, factor) select 63, f.unit_id, t.unit_id, 48 as factor from units f,units t where lower(f.name) = 'cup' and lower(t.name) = 'teaspoon';



select modifier_type, modifier as text, reference_id from modifier_mappings
where modifier_type <> 'Unit';

private ModifierType modifierType;
private String text;
private Long referenceId;

select distinct modifier_type, modifier , reference_id  from modifier_mappings  where modifier_type <> 'Unit'

select distinct u.id from units u join domain_unit du on du.unit_id = t.unit_id where u.id > 0   and du.domain_type = 'UK'

select * from tag where name like '%onion%'