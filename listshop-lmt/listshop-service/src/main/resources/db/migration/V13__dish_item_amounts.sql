alter table dish_items add column whole_quantity int;
alter table dish_items add column fractional_quantity varchar(56);
alter table dish_items add column quantity double precision;
alter table dish_items add column unit_id bigint;
alter table dish_items add column marker varchar(256);
alter table dish_items add column unit_size varchar(256);
alter table dish_items add column raw_modifiers text;
