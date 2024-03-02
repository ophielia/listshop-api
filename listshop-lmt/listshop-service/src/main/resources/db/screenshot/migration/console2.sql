select foodentity0_.food_id     as food_id1_11_,
       foodentity0_.category_id as category2_11_,
       foodentity0_.fdc_id      as fdc_id3_11_,
       foodentity0_.has_factor  as has_fact4_11_,
       foodentity0_.name        as name5_11_
from foods foodentity0_
where (lower(foodentity0_.name) like lower('%baking powder%'))
  --and foodentity0_.has_factor = true

select * from food_conversions where food_id = 21688

select food_id, unit_name from food_conversions;

select * from foods where fdc_id = 172026
--172026
select has_factor, count(*) from foods
group by 1

update tag set food_id = null where tag_id = 185;

delete from factors where tag_id = 13;
select * from units where unit_id = 1000
update tag set food_id = null where tag_id = 13;

select * from food_conversions where food_id = 29904

select * from foods where fdc_id =169707;

select conversion0_.factor_id as factor_i1_7_,
       conversion0_.factor    as factor2_7_,
       conversion0_.from_unit as from_uni4_7_,
       conversion0_.tag_id    as tag_id3_7_,
       conversion0_.to_unit   as to_unit5_7_
from factors conversion0_
where conversion0_.tag_id = 185

select foodentity0_.food_id     as food_id1_11_,
       foodentity0_.category_id as category2_11_,
       foodentity0_.fdc_id      as fdc_id3_11_,
       foodentity0_.has_factor  as has_fact4_11_,
       foodentity0_.name        as name5_11_
from foods foodentity0_
where (lower(foodentity0_.name) like lower('%oregano%'))
  and foodentity0_.has_factor = true

select * from tag where tag_id = 185;
update foods set has_factor = true;

select * from factors where tag_id = 185

select * from food_conversions where food_id = 27801;

select foodentity0_.food_id     as food_id1_11_,
       foodentity0_.category_id as category2_11_,
       foodentity0_.fdc_id      as fdc_id3_11_,
       foodentity0_.has_factor  as has_fact4_11_,
       foodentity0_.name        as name5_11_
from foods foodentity0_
where (lower(foodentity0_.name) like lower('%rice%'))
  --and foodentity0_.has_factor = true

update foods set has_factor = true;

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
  and tagentity1_.is_group=?
  and tagentity1_.to_delete = false


select foodcatego0_.food_category_mapping_id as food_cat1_9_, foodcatego0_.category_id as category2_9_, foodcatego0_.tag_id as tag_id3_9_ from food_category_mapping foodcatego0_ where foodcatego0_.tag_id=33


select t.tag_id, t.name, m.category_id, c.name from tag t
    left outer join food_category_mapping m on m.tag_id = t.tag_id
    left outer join food_categories c on c.category_id = m.category_id
where is_group = true;



delete from factors;
delete from units;



insert into public.factors (factor_id, factor, to_unit, from_unit)
values  (1336, 0.0625, 1005, 1017),
        (1337, 0.5, 1006, 1017),
        (1338, 0.25, 1010, 1017),
        (1339, 0.125, 1017, 1007),
        (1340, 0.0078125, 1005, 1007),
        (1341, 0.0625, 1006, 1007),
        (1342, 0.03125, 1010, 1007),
        (1343, 0.001, 1014, 1013),
        (1344, 0.0022, 1008, 1013),
        (1345, 0.0352733686067019, 1009, 1013),
        (1346, 2.20462262, 1008, 1014),
        (1347, 35.2733686067019, 1009, 1014),
        (1348, 100, 1015, 1003),
        (1349, 4.22675, 1017, 1003),
        (1350, 0.26417, 1005, 1003),
        (1351, 1000, 1004, 1003),
        (1352, 2.1133764, 1006, 1003),
        (1353, 1.05668821, 1010, 1003),
        (1354, 0.00422675, 1017, 1004),
        (1355, 0.00026417, 1005, 1004),
        (1356, 0.0021133764, 1006, 1004),
        (1357, 0.0010566882, 1010, 1004),
        (1358, 0.125, 1005, 1006),
        (1359, 0.5, 1010, 1006),
        (1360, 0.25, 1005, 1010),
        (1361, 0.0000022, 1008, 1016),
        (1362, 0.00003527, 1009, 1016),
        (1363, 0.0105668821, 1010, 1015),
        (1364, 0.0422675, 1017, 1015),
        (1365, 0.0026417205124156, 1005, 1015),
        (1366, 0.021133764, 1006, 1015),
        (1367, 0.001, 1003, 1004),
        (1368, 0.1, 1015, 1004),
        (1369, 0.01, 1003, 1015),
        (1370, 10, 1004, 1015),
        (1371, 0.0625, 1008, 1009),
        (1372, 16, 1009, 1008),
        (1373, 0.3333333333, 1021, 1019),
        (1374, 0.33333333333, 1001, 1002),
        (1375, 0.020833333333, 1000, 1002),
        (1376, 0.020833333333, 1017, 1019),
        (1377, 0.020833333333, 1000, 1002),
        (1378, 0.0625, 1000, 1001),
        (1379, 0.0625, 1017, 1021),
        (1380, 3, 1002, 1001),
        (1381, 3, 1019, 1021),
        (1382, 0.00390625, 1005, 1021),
        (1383, 0.03125, 1006, 1021),
        (1384, 0.5, 1007, 1021),
        (1385, 0.015625, 1010, 1021),
        (1386, 0.00130208333, 1005, 1019),
        (1387, 0.0104166666666, 1006, 1019),
        (1388, 0.1666666666, 1007, 1019),
        (1389, 0.00520833333333, 1010, 1019),
        (1390, 0.0078125, 1007, 1005),
        (1391, 16, 1007, 1006),
        (1392, 8, 1007, 1017),
        (1393, 33.8140227, 1007, 1003),
        (1394, 0.033814023, 1007, 1004),
        (1395, 0.33814203, 1007, 1015);

insert into public.units (unit_id, type, subtype, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume)
values  (1000, 'HYBRID', 'SOLID', 'cup', false, false, true, false, false),
        (1001, 'HYBRID', 'SOLID', 'tablespoon', false, false, true, false, false),
        (1002, 'HYBRID', 'SOLID', 'teaspoon', false, false, true, false, false),
        (1003, 'METRIC', 'VOLUME', 'liter', true, true, true, false, false),
        (1004, 'METRIC', 'VOLUME', 'milliliter', true, true, true, false, false),
        (1005, 'US', 'VOLUME', 'gallon', true, true, true, false, false),
        (1006, 'US', 'VOLUME', 'pint', true, false, true, false, false),
        (1007, 'US', 'VOLUME', 'fl oz', true, true, true, false, false),
        (1008, 'US', 'WEIGHT', 'lb', false, true, true, false, false),
        (1009, 'US', 'WEIGHT', 'oz', false, true, true, false, false),
        (1010, 'US', 'VOLUME', 'quart', true, true, true, false, false),
        (1011, 'UNIT', 'NONE', 'unit', false, true, true, false, false),
        (1013, 'METRIC', 'WEIGHT', 'gram', false, true, true, false, false),
        (1014, 'METRIC', 'WEIGHT', 'kilogram', false, true, true, false, false),
        (1015, 'METRIC', 'VOLUME', 'centiliter', true, false, true, false, false),
        (1016, 'METRIC', 'WEIGHT', 'milligram', false, true, true, false, false),
        (1017, 'US', 'VOLUME', 'cup (fluid)', true, false, true, false, false),
        (1019, 'HYBRID', 'LIQUID', 'teaspoon (fluid)', true, false, true, false, false),
        (1021, 'HYBRID', 'LIQUID', 'tablespoon (fluid)', true, false, true, false, false);

insert into factors (factor_id, from_unit, to_unit, factor) select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 1000 as factor from units f,units t where lower(f.name) = 'kilogram' and lower(t.name) = 'gram';

