alter table foods add column conversion_id bigint;
alter table foods add column original_name varchar(256);
alter table foods add column integral varchar(256);

alter table food_conversions add column food_conversion_id bigint;
alter table food_conversions add column integral varchar(256);
alter table food_conversions add column marker varchar(256);
alter table food_conversions add column sub_amount varchar(256);
alter table food_conversions add column info varchar(256);

alter table tag rename column food_id to conversion_id;
alter table tag add column marker varchar(256);

alter table factors rename column tag_id to conversion_id;
alter table factors add column reference_id bigint;
alter table factors add column marker varchar(256);

alter table units add column is_tag_specific bool;



