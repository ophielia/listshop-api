alter table dish_items drop column whole_quantity ;
alter table dish_items drop column fractional_quantity;
alter table dish_items drop column quantity ;
alter table dish_items drop column unit_id ;
alter table dish_items drop column marker ;
alter table dish_items drop column unit_size ;
alter table dish_items drop column raw_modifiers;
alter table dish_items drop column modifiers_processed;

drop table modifier_mappings;
drop SEQUENCE modifier_mapping_sequence;

select * from flyway_schema_history;

