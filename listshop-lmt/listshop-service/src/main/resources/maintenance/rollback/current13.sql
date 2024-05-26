alter table dish_items drop column whole_quantity ;
alter table dish_items drop column fractional_quantity;
alter table dish_items drop column quantity ;
alter table dish_items drop column unit_id ;
alter table dish_items drop column marker ;
alter table dish_items drop column unit_size ;
alter table dish_items drop column raw_modifiers;
alter table dish_items drop column modifiers_processed;

alter table user_properties drop column if exists  is_system;

drop table modifier_mappings;
drop SEQUENCE modifier_mapping_sequence;

drop table if exists domain_unit;
drop SEQUENCE if exists domain_unit_sequence;

select * from flyway_schema_history;

