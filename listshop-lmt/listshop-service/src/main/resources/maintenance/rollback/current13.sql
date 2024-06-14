alter table dish_items drop column  if exists whole_quantity ;
alter table dish_items drop column  if exists fractional_quantity;
alter table dish_items drop column  if exists quantity ;
alter table dish_items drop column  if exists unit_id ;
alter table dish_items drop column  if exists marker ;
alter table dish_items drop column  if exists unit_size ;
alter table dish_items drop column  if exists raw_modifiers;
alter table dish_items drop column  if exists raw_entry;
alter table dish_items drop column  if exists modifiers_processed;

alter table user_properties drop column if exists  is_system;

drop table if exists modifier_mappings;
drop SEQUENCE if exists modifier_mapping_sequence;

drop table if exists domain_unit;
drop SEQUENCE if exists domain_unit_sequence;

select * from flyway_schema_history;

