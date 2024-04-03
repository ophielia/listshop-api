
alter table tag rename column food_id to conversion_id;

alter table foods rename column marker to integral;

alter table food_conversions drop column fdc_id;

alter table food_conversions add column integral varchar(256);
alter table food_conversions add column sub_amount varchar(256);
alter table food_conversions add column info varchar(256);
alter table food_conversions add column marker varchar(255);


create table food_entry
(
 entry_id bigint,
 food_id bigint,
 fdc_id bigint,
 category_id bigint,
 name text,
 marker varchar(255)
);

create table markers
(
    marker varchar(256)
);

