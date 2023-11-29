insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.5 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.25 as factor
from units f,
     units t
where lower(f.name) = 'cup'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.125 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0078125 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 29.57 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'grams';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0625 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.03125 as factor
from units f,
     units t
where lower(f.name) = 'fl oz'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor
from units f,
     units t
where lower(f.name) = 'gram'
  and lower(t.name) = 'kilogram';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0022 as factor
from units f,
     units t
where lower(f.name) = 'gram'
  and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0352733686067019 as factor
from units f,
     units t
where lower(f.name) = 'gram'
  and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0353 as factor
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
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 35.2733686067019 as factor
from units f,
     units t
where lower(f.name) = 'kilogram'
  and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.01 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 4.22675 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.26417 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'milliliter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 2.1133764 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 1.05668821 as factor
from units f,
     units t
where lower(f.name) = 'liter'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00422675 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00026417 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0021133764 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0010566882 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 28.35 as factor
from units f,
     units t
where lower(f.name) = 'oz'
  and lower(t.name) = 'gram';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.125 as factor
from units f,
     units t
where lower(f.name) = 'pint'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.5 as factor
from units f,
     units t
where lower(f.name) = 'pint'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.25 as factor
from units f,
     units t
where lower(f.name) = 'quart'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0000022 as factor
from units f,
     units t
where lower(f.name) = 'milligram'
  and lower(t.name) = 'lb';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.00003527 as factor
from units f,
     units t
where lower(f.name) = 'milligram'
  and lower(t.name) = 'oz';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0105668821 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'quart';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0422675 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'cup';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.0026417205124156 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'gallon';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.021133764 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'pint';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.001 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.1 as factor
from units f,
     units t
where lower(f.name) = 'milliliter'
  and lower(t.name) = 'centiliter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 0.01 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'liter';
insert into factors (factor_id, from_unit, to_unit, factor)
select nextval('factor_sequence') as newid, f.unit_id, t.unit_id, 10 as factor
from units f,
     units t
where lower(f.name) = 'centiliter'
  and lower(t.name) = 'milliliter';

select *
from factors;