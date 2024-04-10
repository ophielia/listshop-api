alter table foods drop column conversion_id ;
alter table foods drop column original_name ;
alter table foods drop column integral ;

alter table food_conversions drop column food_conversion_id ;
alter table food_conversions drop column integral ;
alter table food_conversions drop column marker ;
alter table food_conversions drop column sub_amount ;
alter table food_conversions drop column info ;

alter table tag rename column conversion_id to food_id ;

alter table factors rename column conversion_id to tag_id ;

select * from flyway_schema_history;

