alter table food_conversions add column fdc_id varchar(256);


update food_conversions c
set fdc_id = f.fdc_id
      from foods f
where f.food_id = c.food_id;
