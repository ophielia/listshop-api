
alter table foods rename column marker to integral;

alter table food_conversions drop column fdc_id;

alter table food_conversions add column integral varchar(256);
alter table food_conversions add column sub_amount varchar(256);
alter table food_conversions add column info varchar(256);
alter table food_conversions add column marker varchar(255);


create table food_entry
(
 entry_id integer,
 food_id bigint,
 new_name text,
 marker varchar(255)
);

create table markers
(
    marker varchar(256)
);



update tag set internal_status = 1;
alter table tag alter column internal_status set not null;

alter table tag add column is_liquid bool;

alter table tag add column food_id bigint;

alter table factors add column tag_id BIGINT;

-- reset sequence for factors to start from 1000
SELECT setval('factor_sequence', 999, true);