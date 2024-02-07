delete from factors;

delete from units;

insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1000, 'cup', 'US', 'VOLUME',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1001, 'tablespoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1002, 'teaspoon', 'HYBRID', 'SOLID',FALSE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1003, 'liter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1004, 'milliliter', 'METRIC', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1005, 'gallon', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1006, 'pint', 'US', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1007, 'fl oz', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1008, 'lb', 'US', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1009, 'oz', 'US', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1010, 'quart', 'US', 'VOLUME',TRUE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1011, 'unit', 'UNIT', 'NONE',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1013, 'gram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1014, 'kilogram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1015, 'centiliter', 'METRIC', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1016, 'milligram', 'METRIC', 'WEIGHT',TRUE, TRUE,FALSE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1017, 'cup (fluid)', 'US', 'VOLUME',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1019, 'teaspoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE);
insert into units (unit_id, name, type, subtype, is_list_unit, is_dish_unit, is_liquid) values (1021, 'tablespoon (fluid)', 'HYBRID', 'LIQUID',FALSE, TRUE,TRUE);

insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0078125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = 'fl oz' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'kilogram';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0022 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0352733686067019 as factor from units f,units t where lower(f.name) = 'gram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2.20462262 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 35.2733686067019 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 100 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 4.22675 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.26417 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2.1133764 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 1.05668821 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00422675 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00026417 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0021133764 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0010566882 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.125 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.25 as factor from units f,units t where lower(f.name) = 'quart' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0000022 as factor from units f,units t where lower(f.name) = 'milligram' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00003527 as factor from units f,units t where lower(f.name) = 'milligram' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0105668821 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0422675 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0026417205124156 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.021133764 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.1 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.01 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 10 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'oz' and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = 'lb' and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.3333333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'tablespoon (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.33333333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'tablespoon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.020833333333 as factor from units f,units t where lower(f.name) = 'teaspoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'tablespoon' and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'cup (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = 'tablespoon' and lower(t.name) = 'teaspoon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 3.0 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'teaspoon (fluid)';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00390625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.03125 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.5 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.015625 as factor from units f,units t where lower(f.name) = 'tablespoon (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00130208333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0104166666666 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.1666666666 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00520833333333 as factor from units f,units t where lower(f.name) = 'teaspoon (fluid)' and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0078125 as factor from units f,units t where lower(f.name) = 'gallon' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 16 as factor from units f,units t where lower(f.name) = 'pint' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 8.0 as factor from units f,units t where lower(f.name) = 'cup (fluid)' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 33.8140227 as factor from units f,units t where lower(f.name) = 'liter' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.033814023 as factor from units f,units t where lower(f.name) = 'milliliter' and lower(t.name) = 'fl oz';
insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.33814203 as factor from units f,units t where lower(f.name) = 'centiliter' and lower(t.name) = 'fl oz';

select * from factors;



select tagrelatio0_.child_tag_id as col_0_0_,
       tagentity1_.name          as col_1_0_,
       tagentity1_.description   as col_2_0_,
       tagentity1_.power         as col_3_0_,
       tagentity1_.user_id       as col_4_0_
from tag_relation tagrelatio0_
         cross join tag tagentity1_
where tagrelatio0_.child_tag_id = tagentity1_.tag_id;


select tagrelatio0_.child_tag_id  as col_0_0_,
       tagentity1_.name           as col_1_0_,
       tagentity1_.description    as col_2_0_,
       tagentity1_.power          as col_3_0_,
       tagentity1_.user_id        as col_4_0_,
       tagentity1_.tag_type       as col_5_0_,
       tagentity1_.is_group       as col_6_0_,
       tagrelatio0_.parent_tag_id as col_7_0_,
       tagentity1_.to_delete      as col_8_0_
from tag_relation tagrelatio0_
         cross join tag tagentity1_
where tagrelatio0_.child_tag_id = tagentity1_.tag_id
  and tagentity1_.user_id=?
  and tagentity1_.is_group=?
  and (tagentity1_.tag_type in (?, ?))
  and tagentity1_.to_delete = false
