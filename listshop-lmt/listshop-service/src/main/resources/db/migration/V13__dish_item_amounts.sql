alter table dish_items add column whole_quantity int;
alter table dish_items add column fractional_quantity varchar(56);
alter table dish_items add column quantity double precision;
alter table dish_items add column unit_id bigint;
alter table dish_items add column marker varchar(256);
alter table dish_items add column unit_size varchar(256);
alter table dish_items add column raw_modifiers varchar(256);
alter table dish_items add column raw_entry text;
alter table dish_items add column modifiers_processed boolean;

alter table user_properties add column if not exists is_system boolean;

CREATE TABLE modifier_mappings
(
    mapping_id bigint                 NOT NULL,
    modifier_type    character varying(50) NOT NULL,
    modifier    character varying(100) NOT NULL,
    mapped_modifier    character varying(100) NOT NULL,
    reference_id bigint
);

CREATE SEQUENCE modifier_mapping_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE TABLE if not exists  domain_unit
(
    domain_unit_id bigint                 NOT NULL,
    domain_type    character varying(50) NOT NULL,
    unit_id    bigint
);

CREATE SEQUENCE if not exists domain_unit_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;