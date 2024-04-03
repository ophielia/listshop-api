alter table tag rename column conversion_id to food_id;

alter table foods rename column integral to marker;

alter table food_conversions add column fdc_id bigint;

alter table food_conversions drop column integral ;
alter table food_conversions drop column sub_amount ;
alter table food_conversions drop column info ;
alter table food_conversions drop column marker ;

drop table food_entry;

drop table markers;


select * from flyway_schema_history;