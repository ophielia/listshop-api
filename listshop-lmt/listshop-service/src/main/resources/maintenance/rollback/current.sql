alter table foods drop column conversion_id ;
alter table foods drop column original_name ;
alter table foods drop column integral ;

alter table food_conversions drop column food_conversion_id ;
alter table food_conversions drop column integral ;
alter table food_conversions drop column marker ;
alter table food_conversions drop column sub_amount ;
alter table food_conversions drop column info ;

alter table tag rename column conversion_id to food_id ;
alter table tag  drop column marker;;

alter table factors rename column conversion_id to tag_id ;
alter table factors drop column reference_id;
--alter table factors add column marker ;

--alter table units drop column is_tag_specific;

select * from flyway_schema_history;

