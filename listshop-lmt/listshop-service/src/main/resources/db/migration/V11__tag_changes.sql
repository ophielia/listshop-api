
alter table tag add column internal_status bigint default 1;
update tag set internal_status = 1;
alter table tag alter column internal_status set not null;

alter table tag add column is_liquid bool;

alter table tag add column food_id bigint;

alter table factors add column tag_id BIGINT;

-- reset sequence for factors to start from 1000
SELECT setval('factor_sequence', 999, true);