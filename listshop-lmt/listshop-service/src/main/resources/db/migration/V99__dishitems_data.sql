-- now the data
insert into dish_items (dish_item_id, dish_id, tag_id)
select nextval('dish_item_sequence'), dish_id, tag_id
from dish_tags;



insert into units (unit_id, name, type)
values (1000, 'cup', 'Imperial');
insert into units (unit_id, name, type)
values (1001, 'tablespoon', 'Hybrid');
insert into units (unit_id, name, type)
values (1002, 'teaspoon', 'Hybrid');
insert into units (unit_id, name, type)
values (1003, 'liter', 'Metric');
insert into units (unit_id, name, type)
values (1004, 'milliliter', 'Metric');
insert into units (unit_id, name, type)
values (1005, 'gallon', 'Imperial');
insert into units (unit_id, name, type)
values (1006, 'pint', 'Imperial');
insert into units (unit_id, name, type)
values (1007, 'fl oz', 'Imperial');
insert into units (unit_id, name, type)
values (1008, 'lb', 'Imperial');
insert into units (unit_id, name, type)
values (1009, 'oz', 'Imperial');
insert into units (unit_id, name, type)
values (1010, 'quart', 'Imperial');
insert into units (unit_id, name, type)
values (1011, 'unit', 'Unit');
insert into units (unit_id, name, type)
values (1012, 'quart', 'Imperial');
insert into units (unit_id, name, type)
values (1013, 'gram', 'Metric');
insert into units (unit_id, name, type)
values (1014, 'kilogram', 'Metric');
insert into units (unit_id, name, type)
values (1015, 'centiliter', 'Metric');


insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 28.35 as factor
from units f,
     units t
where lower(f.name) = 'oz'
  and lower(t.name) = 'gram';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 35.2733686067019 as factor
from units f,
     units t
where lower(f.name) = 'kilogram'
  and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0352733686067019 as factor
from units f,
     units t
where lower(f.name) = 'gram'
  and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2.20462262 as factor
from units f,
     units t
where lower(f.name) = 'kilogram'
  and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 4 as factor
from units f,
     units t
where lower(f.name) = 'quart'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 8 as factor
from units f,
     units t
where lower(f.name) = 'pint'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 16 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 128 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 3.8 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2 as factor
from units f,
     units t
where lower(f.name) = 'pint'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 4 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 32 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.95 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 16 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.475 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 8 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.24 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.01 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'centiliter';